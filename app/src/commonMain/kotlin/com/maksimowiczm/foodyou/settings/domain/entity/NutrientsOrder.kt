package com.maksimowiczm.foodyou.settings.domain.entity

enum class NutrientsOrder {
    Proteins,
    Fats,
    Carbohydrates,
    Other,
    Vitamins,
    Minerals;

    companion object {
        val defaultOrder: List<NutrientsOrder>
            get() = listOf(Proteins, Fats, Carbohydrates, Other, Vitamins, Minerals)
    }
}
