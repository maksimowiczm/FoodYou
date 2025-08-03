package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.compose.ui.text.intl.Locale
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import co.touchlab.kermit.Logger
import foodyou.feature.fooddiary.generated.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class InitializeMealsCallback : RoomDatabase.Callback() {

    override fun onCreate(connection: SQLiteConnection) {
        val meals = getMeals()

        connection.execSQL("BEGIN TRANSACTION;")

        try {
            meals.forEach { meal ->
                val query = """
                    INSERT INTO Meal (name, fromHour, fromMinute, toHour, toMinute, rank) 
                    VALUES ($1, $2, $3, $4, $5, $6)
                """.trimIndent()

                val statement = connection.prepare(query)

                statement.bindText(1, meal.name)
                statement.bindInt(2, meal.fromHour)
                statement.bindInt(3, meal.fromMinute)
                statement.bindInt(4, meal.toHour)
                statement.bindInt(5, meal.toMinute)
                statement.bindInt(6, meal.rank)

                statement.step()

                statement.close()
            }

            connection.execSQL("COMMIT;")
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Failed to insert meals" }
            connection.execSQL("ROLLBACK;")
        }
    }

    fun getMeals(): List<Meal> = runBlocking {
        val tag = Locale.current.toLanguageTag()

        val content = try {
            Res.readBytes("files/meals-$tag.json")
        } catch (e: Exception) {
            Logger.w(TAG, e) {
                "Failed to read meals file for locale $tag, falling back to default"
            }

            Res.readBytes("files/meals.json")
        }

        val meals = Json
            .decodeFromString<List<MealJson>>(content.decodeToString())
            .mapIndexed { index, mealJson ->
                Meal(
                    name = mealJson.name,
                    fromHour = mealJson.from.substringBefore(':').toInt(),
                    fromMinute = mealJson.from.substringAfter(':').toInt(),
                    toHour = mealJson.to.substringBefore(':').toInt(),
                    toMinute = mealJson.to.substringAfter(':').toInt(),
                    rank = index
                )
            }

        return@runBlocking meals
    }

    @Serializable
    private data class MealJson(val name: String, val from: String, val to: String)

    companion object {
        const val TAG = "InitializeMealsCallback"
    }
}
