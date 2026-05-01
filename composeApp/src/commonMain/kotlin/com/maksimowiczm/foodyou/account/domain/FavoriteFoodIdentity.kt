package com.maksimowiczm.foodyou.account.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FavoriteFoodIdentity {
    @Serializable @JvmInline value class FoodDataCentral(val fdcId: Int) : FavoriteFoodIdentity

    @Serializable @JvmInline value class OpenFoodFacts(val barcode: String) : FavoriteFoodIdentity

    @Serializable @JvmInline value class UserProduct(val id: Uuid) : FavoriteFoodIdentity
}
