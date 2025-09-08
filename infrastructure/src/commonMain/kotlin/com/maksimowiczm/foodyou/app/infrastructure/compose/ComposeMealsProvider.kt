package com.maksimowiczm.foodyou.app.infrastructure.compose

import com.maksimowiczm.foodyou.app.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.MealEntity
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.MealsProvider
import com.maksimowiczm.foodyou.shared.common.FoodYouLogger
import foodyou.infrastructure.generated.resources.Res
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class ComposeMealsProvider(private val systemDetails: SystemDetails) : MealsProvider {
    override fun getMeals(): List<MealEntity> = runBlocking {
        val tag = runBlocking { systemDetails.languageTag.first() }

        val content =
            try {
                Res.readBytes("files/meals/meals-$tag.json")
            } catch (e: Exception) {
                FoodYouLogger.w(TAG, e) {
                    "Failed to read meals file for locale $tag, falling back to default"
                }

                Res.readBytes("files/meals/meals.json")
            }

        val meals =
            Json.Default.decodeFromString<List<MealJson>>(content.decodeToString()).mapIndexed {
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
