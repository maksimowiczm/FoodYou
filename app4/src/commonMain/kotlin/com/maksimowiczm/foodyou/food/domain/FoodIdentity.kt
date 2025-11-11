package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlin.jvm.JvmInline

sealed interface FoodIdentity

sealed interface FoodProductIdentity : FoodIdentity {
    /** Local database identifier */
    data class Local(val id: String, val accountId: LocalAccountId) : FoodProductIdentity

    /** Open Food Facts identifier (barcode) */
    @JvmInline value class OpenFoodFacts(val barcode: String) : FoodProductIdentity

    /** FoodData Central identifier */
    @JvmInline value class FoodDataCentral(val fdcId: Int) : FoodProductIdentity
}

data class LocalFoodRecipeIdentity(val id: Long, val accountId: LocalAccountId) : FoodIdentity
