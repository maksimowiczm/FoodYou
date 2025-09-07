package com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.shared.domain.log.Logger

fun interface MealsProvider {
    fun getMeals(): List<MealEntity>
}

class InitializeMealsCallback(private val provider: MealsProvider, private val logger: Logger) :
    RoomDatabase.Callback() {

    override fun onCreate(connection: SQLiteConnection) {
        val meals = provider.getMeals()

        connection.execSQL("BEGIN TRANSACTION;")

        try {
            meals.forEach { meal ->
                val query =
                    """
                    INSERT INTO Meal (name, fromHour, fromMinute, toHour, toMinute, rank) 
                    VALUES ($1, $2, $3, $4, $5, $6)
                """
                        .trimIndent()

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
            logger.e(TAG, e) { "Failed to insert meals" }
            connection.execSQL("ROLLBACK;")
        }
    }

    companion object {
        const val TAG = "InitializeMealsCallback"
    }
}
