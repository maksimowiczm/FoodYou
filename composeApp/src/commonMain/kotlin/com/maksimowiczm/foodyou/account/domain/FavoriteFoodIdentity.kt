package com.maksimowiczm.foodyou.account.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

sealed interface FavoriteFoodIdentity {
    @JvmInline value class FoodDataCentral(val fdcId: Int) : FavoriteFoodIdentity

    @JvmInline value class OpenFoodFacts(val barcode: String) : FavoriteFoodIdentity

    @JvmInline value class UserProduct(val id: Uuid) : FavoriteFoodIdentity
}
