package com.maksimowiczm.foodyou.database.callback

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maksimowiczm.foodyou.database.entity.MealEntity

abstract class InitializeMealsCallback : RoomDatabase.Callback() {
    abstract fun getMeals(): List<MealEntity>

    override fun onCreate(db: SupportSQLiteDatabase) {
        val meals = getMeals()

        db.beginTransaction()

        try {
            meals.forEach { meal ->
                db.execSQL(
                    "INSERT INTO MealEntity (name, fromHour, fromMinute, toHour, toMinute) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any>(
                        meal.name,
                        meal.fromHour,
                        meal.fromMinute,
                        meal.toHour,
                        meal.toMinute
                    )
                )
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert meals", e)
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        const val TAG = "InitializeMealsCallback"
    }
}
