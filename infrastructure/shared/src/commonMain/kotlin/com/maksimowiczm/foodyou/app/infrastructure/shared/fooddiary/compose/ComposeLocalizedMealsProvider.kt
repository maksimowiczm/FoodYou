package com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary.compose

import com.maksimowiczm.foodyou.app.infrastructure.shared.SystemDetails
import com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary.LocalizedMealsProvider
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import foodyou.infrastructure.shared.generated.resources.Res
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class ComposeLocalizedMealsProvider(
    private val systemDetails: SystemDetails,
    private val logger: Logger,
) : LocalizedMealsProvider {
    override suspend fun getMeals(): List<LocalizedMealsProvider.LocalizedMeal> {
        val tag = runBlocking { systemDetails.languageTag.first() }

        val content =
            try {
                Res.readBytes("files/meals/meals-$tag.json")
            } catch (e: Exception) {
                logger.w(TAG, e) {
                    "Failed to read meals file for locale $tag, falling back to default"
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
        private const val TAG = "ComposeLocalizedMealsProvider"
    }
}
