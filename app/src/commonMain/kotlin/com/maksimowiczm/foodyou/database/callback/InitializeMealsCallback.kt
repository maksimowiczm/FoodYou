package com.maksimowiczm.foodyou.database.callback

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.database.entity.MealEntity

expect class InitializeMealsCallback : InitializeMealsCallbackBase {
    override fun getMeals(): List<MealEntity>
}

abstract class InitializeMealsCallbackBase : RoomDatabase.Callback() {
    abstract fun getMeals(): List<MealEntity>

    override fun onCreate(connection: SQLiteConnection) {
        val meals = getMeals()

        connection.execSQL("BEGIN TRANSACTION;")

        try {
            meals.forEach { meal ->
                val query = """
                    INSERT INTO MealEntity (name, fromHour, fromMinute, toHour, toMinute, rank) 
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

    companion object {
        const val TAG = "InitializeMealsCallback"
    }
}
