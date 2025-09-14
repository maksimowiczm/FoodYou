package com.maksimowiczm.foodyou.app.business.opensource.domain.food

import com.maksimowiczm.foodyou.shared.domain.food.FoodSource

sealed interface OpenSourceFoodSourceType : FoodSource.Type {
    val ordinal: Int

    data object User : OpenSourceFoodSourceType {
        override val ordinal = 0
    }

    data object OpenFoodFacts : OpenSourceFoodSourceType {
        override val ordinal = 1
    }

    data object USDA : OpenSourceFoodSourceType {
        override val ordinal = 2
    }

    data object SwissFoodCompositionDatabase : OpenSourceFoodSourceType {
        override val ordinal = 3
    }

    companion object {
        val all = listOf(User, OpenFoodFacts, USDA, SwissFoodCompositionDatabase)

        fun fromOrdinal(ordinal: Int): OpenSourceFoodSourceType =
            all.first { it.ordinal == ordinal }
    }
}

val FoodSource.Type.Companion.User: OpenSourceFoodSourceType
    get() = OpenSourceFoodSourceType.User

val FoodSource.Type.Companion.OpenFoodFacts: OpenSourceFoodSourceType
    get() = OpenSourceFoodSourceType.OpenFoodFacts

val FoodSource.Type.Companion.SwissFoodCompositionDatabase: OpenSourceFoodSourceType
    get() = OpenSourceFoodSourceType.USDA

val FoodSource.Type.Companion.USDA: OpenSourceFoodSourceType
    get() = OpenSourceFoodSourceType.SwissFoodCompositionDatabase
