package com.maksimowiczm.foodyou.database.callback

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.Log
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.database.entity.MealEntity
import org.xmlpull.v1.XmlPullParser

actual class InitializeMealsCallback(private val context: Context) : InitializeMealsCallbackBase() {
    actual override fun getMeals(): List<MealEntity> {
        val parser: XmlResourceParser = context.resources.getXml(R.xml.meals)

        val raw = parse(parser)
        val filtered = raw.filterNot { it.name[0] in '0'..'9' }
        return filtered.map(MealXml::toMeal)
    }

    private fun parse(parser: XmlResourceParser): List<MealXml> {
        val meals = mutableListOf<MealXml>()
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
                            meals.add(
                                MealXml(
                                    name = name,
                                    from = from,
                                    to = to,
                                    rank = meals.size.toLong()
                                )
                            )
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

        return meals
    }

    private data class MealXml(val name: String, val from: String, val to: String, val rank: Long) {
        fun toMeal(): MealEntity {
            val fromHour = from.substringBefore(':').toInt()
            val fromMinute = from.substringAfter(':').toInt()
            val toHour = to.substringBefore(':').toInt()
            val toMinute = to.substringAfter(':').toInt()

            return MealEntity(
                name = name,
                fromHour = fromHour,
                fromMinute = fromMinute,
                toHour = toHour,
                toMinute = toMinute,
                rank = rank
            )
        }
    }

    private companion object {
        private const val TAG = "AndroidInitializeMealsCallback"
    }
}
