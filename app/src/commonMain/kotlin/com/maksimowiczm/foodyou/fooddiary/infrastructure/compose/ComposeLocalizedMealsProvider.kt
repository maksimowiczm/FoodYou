package com.maksimowiczm.foodyou.fooddiary.infrastructure.compose

import com.maksimowiczm.foodyou.app.generated.resources.Res
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.fooddiary.domain.service.LocalizedMealsProvider
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class ComposeLocalizedMealsProvider(private val logger: Logger) : LocalizedMealsProvider {
    override suspend fun getMeals(languageTag: String): List<LocalizedMealsProvider.LocalizedMeal> {
        val content =
            try {
                Res.readBytes("files/meals/meals-$languageTag.json")
            } catch (e: Exception) {
                logger.w(TAG, e) {
                    "Failed to read meals file for locale $languageTag, falling back to default"
                }

                Res.readBytes("files/meals/meals.json")
            }

        return Json.decodeFromString<List<MealJson>>(content.decodeToString()).map { mealJson ->
            LocalizedMealsProvider.LocalizedMeal(
                name = mealJson.name,
                start =
                    LocalTime(
                        hour = mealJson.from.substringBefore(':').toInt(),
                        minute = mealJson.from.substringAfter(':').toInt(),
                    ),
                end =
                    LocalTime(
                        hour = mealJson.to.substringBefore(':').toInt(),
                        minute = mealJson.to.substringAfter(':').toInt(),
                    ),
            )
        }
    }

    @Serializable private data class MealJson(val name: String, val from: String, val to: String)

    private companion object {
        const val TAG = "ComposeLocalizedMealsProvider"
    }
}
