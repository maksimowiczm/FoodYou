package com.maksimowiczm.foodyou.infrastructure

import androidx.compose.ui.text.intl.Locale
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.MealEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.MealsProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class ComposeMealsProvider : MealsProvider {
    override fun getMeals(): List<MealEntity> = runBlocking {
        val tag = Locale.current.toLanguageTag()

        val content =
            try {
                Res.readBytes("files/meals-$tag.json")
            } catch (e: Exception) {
                FoodYouLogger.w(TAG, e) {
                    "Failed to read meals file for locale $tag, falling back to default"
                }

                Res.readBytes("files/meals.json")
            }

        val meals =
            Json.decodeFromString<List<MealJson>>(content.decodeToString()).mapIndexed {
                index,
                mealJson ->
                MealEntity(
                    name = mealJson.name,
                    fromHour = mealJson.from.substringBefore(':').toInt(),
                    fromMinute = mealJson.from.substringAfter(':').toInt(),
                    toHour = mealJson.to.substringBefore(':').toInt(),
                    toMinute = mealJson.to.substringAfter(':').toInt(),
                    rank = index,
                )
            }

        return@runBlocking meals
    }

    @Serializable private data class MealJson(val name: String, val from: String, val to: String)

    private companion object {
        private const val TAG = "ComposeMealsProvider"
    }
}
