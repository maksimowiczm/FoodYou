package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime

object MealTemplates {
    /** Evaluates the template for [language]. */
    fun forLanguage(language: Language) = MealPlan(MealBuilder().forLanguage(language).build())
}

/**
 * Creates the default meal configuration for a given [language].
 *
 * To add support for a new language:
 *
 * 1. Add a new branch in `MealBuilder.forLanguage`.
 * 2. Define the meals that are commonly used in that language/culture.
 * 3. Define time ranges that reflect typical eating habits.
 *
 * Example:
 * ```
 * Language.Example -> {
 *     breakfast from "06:00" until "10:00"
 *     lunch from "12:00" until "15:00"
 *     dinner from "18:00" until "21:00"
 *     snacks
 * }
 * ```
 *
 * Standard meal types:
 * - `breakfast`
 * - `secondBreakfast`
 * - `lunch`
 * - `afternoonSnack`
 * - `dinner`
 * - `snacks`
 *
 * Additional custom meals can be created by using a string:
 * ```
 * "Brunch" from "10:00" until "12:00"
 * ```
 */
private fun MealBuilder.forLanguage(language: Language): MealBuilder = apply {
    when (language) {
        Language.Polish -> {
            breakfast from "06:00" until "10:00"
            secondBreakfast from "10:00" until "12:00"
            lunch from "12:00" until "18:00"
            dinner from "18:00" until "00:00"
            snacks
        }

        Language.Hungarian -> {
            breakfast from "06:00" until "08:00"
            secondBreakfast from "09:30" until "10:30"
            lunch from "11:00" until "14:00"
            afternoonSnack from "15:30" until "17:00"
            dinner from "18:00" until "21:00"
            snacks
        }

        else -> englishMeals()
    }
}

private fun MealBuilder.englishMeals(): MealBuilder = apply {
    breakfast from "06:00" until "10:00"
    lunch from "10:00" until "15:00"
    dinner from "15:00" until "21:00"
    snacks
}

/**
 * Builder used to define the default meals for a language.
 *
 * The DSL is intentionally designed so that meal definitions read almost like natural language.
 *
 * Standard meals:
 * ```
 * breakfast from "06:00" until "10:00"
 * lunch from "12:00" until "15:00"
 * dinner from "18:00" until "21:00"
 * snacks
 * ```
 *
 * Notes:
 * - Times use the 24-hour format (`HH:mm`).
 * - Meals are added in the order they are declared.
 * - `snacks` creates an all-day meal category without a time range.
 */
private class MealBuilder {
    private val meals = mutableListOf<Meal>()

    inner class SlotStart(private val slot: MealType) {
        infix fun from(time: String): SlotRange = SlotRange(slot, time.toLocalTime())

        fun allDay(): MealBuilder =
            this@MealBuilder.apply {
                meals.add(
                    Meal.Standard(
                        identity = MealIdentity(Uuid.random()),
                        timeWindow = Meal.TimeWindow.AllDay,
                        mealType = slot,
                    )
                )
            }
    }

    inner class SlotRange(
        val slot: MealType,
        val from: LocalTime,
    ) {
        fun append(until: LocalTime): MealBuilder =
            this@MealBuilder.apply {
                meals.add(
                    Meal.Standard(
                        identity = MealIdentity(Uuid.random()),
                        timeWindow = Meal.TimeWindow.Range(from, until),
                        mealType = slot,
                    )
                )
            }

        infix fun until(time: String): MealBuilder = append(time.toLocalTime())
    }

    val breakfast
        get() = SlotStart(MealType.Breakfast)

    val secondBreakfast
        get() = SlotStart(MealType.SecondBreakfast)

    val lunch
        get() = SlotStart(MealType.Lunch)

    val afternoonSnack
        get() = SlotStart(MealType.AfternoonSnack)

    val dinner
        get() = SlotStart(MealType.Dinner)

    val snacks
        get() = SlotStart(MealType.Snacks).allDay()

    fun build(): List<Meal> = meals.toList()
}

private fun String.toLocalTime(): LocalTime {
    val (h, m) = split(":").map { it.toInt() }
    return LocalTime(h, m)
}
