package com.maksimowiczm.foodyou.core.feature.addfood.database

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maksimowiczm.foodyou.core.R
import org.xmlpull.v1.XmlPullParser

class InitializeMealsCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        val meals = mutableListOf<Meal>()
        val parser: XmlResourceParser = context.resources.getXml(R.xml.meals)

        var name: String? = null
        var from: String? = null
        var to: String? = null

        try {
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                when (parser.eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "name" -> name = parser.nextText()
                            "from" -> from = parser.nextText()
                            "to" -> to = parser.nextText()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "meal" && name != null && from != null && to != null) {
                            meals.add(Meal(name, from, to))
                            name = null
                            from = null
                            to = null
                        }
                    }
                }
                parser.next()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse meals", e)
        } finally {
            parser.close()
        }

        db.beginTransaction()

        try {
            meals.forEach { meal ->
                db.execSQL(
                    "INSERT INTO MealEntity (name, fromHour, fromMinute, toHour, toMinute) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any>(
                        meal.name,
                        meal.from.substringBefore(':').toInt(),
                        meal.from.substringAfter(':').toInt(),
                        meal.to.substringBefore(':').toInt(),
                        meal.to.substringAfter(':').toInt()
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

    private data class Meal(
        val name: String,
        val from: String,
        val to: String
    )

    private companion object {
        private const val TAG = "InitializeMealsCallback"
    }
}
