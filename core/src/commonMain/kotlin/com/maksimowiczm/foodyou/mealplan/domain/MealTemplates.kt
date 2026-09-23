package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlinx.datetime.LocalTime

object MealTemplates {
    /** Evaluates the template for [language]. */
    fun forLanguage(language: Language) = MealBuilder().forLanguage(language).build()
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
 *     "Breakfast" from "06:00" until "10:00"
 *     "Lunch" from "12:00" until "15:00"
 *     "Dinner" from "18:00" until "21:00"
 *     "Snacks".allDay()
 * }
 * ```
 */
private fun MealBuilder.forLanguage(language: Language): MealBuilder = apply {
    when (language) {
        Language.English -> englishMeals()
        Language.Arabic -> englishMeals("فطور", "غداء", "عشاء", "التصبيرات")
        Language.Catalan -> englishMeals("Esmorzar", "Dinar", "Sopar", "Snacks")
        Language.ChineseSimplified -> englishMeals("早餐", "午餐", "晚餐", "零食")
        Language.Czech -> englishMeals("Snídaně", "Oběd", "Večeře", "Svačiny")
        Language.Danish -> englishMeals("Morgenmad", "Frokost", "Aftensmad", "Snacks")
        Language.Dutch -> englishMeals("Ontbijt", "Lunch", "Avondeten", "Tussendoortjes")
        Language.French -> englishMeals("Petit-déjeuner", "Déjeuner", "Dîner", "Collation")
        Language.German -> englishMeals("Frühstück", "Mittagessen", "Abendessen", "Snacks")
        Language.Hungarian -> {
            "Reggeli" from "06:00" until "08:00"
            "Tízórai" from "09:30" until "10:30"
            "Ebéd" from "11:00" until "14:00"
            "Uzsonna" from "15:30" until "17:00"
            "Vacsora" from "18:00" until "21:00"
            "Snacks".allDay()
        }

        Language.Indonesian -> englishMeals("Sarapan", "Makan Siang", "Makan Malam", "Cemilan")
        Language.Italian -> englishMeals("Colazione", "Pranzo", "Cena", "Spuntini")
        Language.Polish -> {
            "Śniadanie" from "06:00" until "10:00"
            "Drugie Śniadanie" from "10:00" until "12:00"
            "Obiad" from "12:00" until "18:00"
            "Kolacja" from "18:00" until "00:00"
            "Przekąski".allDay()
        }

        Language.PortugueseBrazil -> englishMeals("Café da manhã", "Almuerzo", "Jantar", "Lanches")
        Language.PortuguesePortugal -> englishMeals("Pequeno-almoço", "Almoço", "Jantar", "Lanches")
        Language.Russian -> englishMeals("Завтрак", "Обед", "Ужин", "Перекусы")
        Language.Slovenian -> englishMeals("Zajtrk", "Kosilo", "Večerja", "Prigrizki")
        Language.Spanish -> englishMeals("Desayuno", "Almuerzo", "Cena", "Snacks")
        Language.Turkish ->
            englishMeals("Kahvaltı", "Öğle Yemeği", "Akşam Yemeği", "Atıştırmalıklar")
        Language.Ukrainian -> englishMeals("Сніданок", "Обід", "Вечеря", "Закуски")
    }
}

private fun MealBuilder.englishMeals(
    breakfast: String = "Breakfast",
    lunch: String = "Lunch",
    dinner: String = "Dinner",
    snacks: String = "Snacks",
): MealBuilder = apply {
    breakfast from "06:00" until "10:00"
    lunch from "10:00" until "15:00"
    dinner from "15:00" until "21:00"
    snacks.allDay()
}

/**
 * Builder used to define the default meals for a language.
 *
 * The DSL is intentionally designed so that meal definitions read almost like natural language.
 *
 * Standard meals:
 * ```
 * "Breakfast" from "06:00" until "10:00"
 * "Lunch" from "12:00" until "15:00"
 * "Dinner" from "18:00" until "21:00"
 * "Snacks".allDay()
 * ```
 *
 * Notes:
 * - Times use the 24-hour format (`HH:mm`).
 * - Meals are added in the order they are declared.
 */
private class MealBuilder {
    private val meals = mutableListOf<Meal>()

    inner class SlotStart(private val name: String) {
        infix fun from(time: String): SlotRange = SlotRange(name, time.toLocalTime())

        fun allDay(): MealBuilder =
            this@MealBuilder.apply {
                meals.add(
                    Meal(
                        name = name,
                        timeWindow = Meal.TimeWindow.AllDay,
                    )
                )
            }
    }

    inner class SlotRange(
        private val name: String,
        private val from: LocalTime,
    ) {
        fun append(until: LocalTime): MealBuilder =
            this@MealBuilder.apply {
                meals.add(
                    Meal(
                        name = name,
                        timeWindow = Meal.TimeWindow.Range(from, until),
                    )
                )
            }

        infix fun until(time: String): MealBuilder = append(time.toLocalTime())
    }

    infix fun String.from(time: String): SlotRange = SlotStart(this) from time

    fun String.allDay(): MealBuilder = SlotStart(this).allDay()

    fun build(): List<Meal> = meals.toList()
}

private fun String.toLocalTime(): LocalTime {
    val (h, m) = split(":").map { it.toInt() }
    return LocalTime(h, m)
}
