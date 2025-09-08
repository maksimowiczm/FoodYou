package com.maksimowiczm.foodyou.business.settings.domain

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
