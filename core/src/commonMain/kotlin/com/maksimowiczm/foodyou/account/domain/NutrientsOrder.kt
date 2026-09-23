package com.maksimowiczm.foodyou.account.domain

import kotlinx.serialization.Serializable

@Serializable
enum class NutrientsOrder {
    Proteins,
    Fats,
    Carbohydrates,
    Other,
    Vitamins,
    Minerals;

    fun isMacronutrient() = this in macronutrients

    companion object {
        val defaultOrder: List<NutrientsOrder>
            get() = listOf(Proteins, Fats, Carbohydrates, Other, Vitamins, Minerals)

        val macronutrients = setOf(Proteins, Fats, Carbohydrates)
    }
}
