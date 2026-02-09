package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.common.system.SystemDetails
import com.maksimowiczm.foodyou.fooddiary.domain.service.LocalizedMealsProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class InitializeMealsCallback(
    private val provider: LocalizedMealsProvider,
    private val systemDetails: SystemDetails,
    private val logger: Logger,
) : RoomDatabase.Callback() {

    override fun onCreate(connection: SQLiteConnection) {
        val meals = runBlocking {
            val languageTag = systemDetails.languageTag.first()

            provider.getMeals(languageTag).mapIndexed { i, (name, start, end) ->
                MealEntity(
                    name = name,
                    fromHour = start.hour,
                    fromMinute = start.minute,
                    toHour = end.hour,
                    toMinute = end.minute,
                    rank = i,
                )
            }
        }

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
