package com.maksimowiczm.foodyou.app.business.shared.domain.settings

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
