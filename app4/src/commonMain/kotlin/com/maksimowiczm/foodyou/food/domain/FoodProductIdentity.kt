package com.maksimowiczm.foodyou.food.domain

import kotlin.jvm.JvmInline

sealed interface FoodProductIdentity {
    /** Local database identifier */
    @JvmInline value class Local(val id: String) : FoodProductIdentity

    /** Open Food Facts identifier (barcode) */
    @JvmInline value class OpenFoodFacts(val barcode: String) : FoodProductIdentity

    /** FoodData Central identifier */
    @JvmInline value class FoodDataCentral(val fdcId: Int) : FoodProductIdentity
}
