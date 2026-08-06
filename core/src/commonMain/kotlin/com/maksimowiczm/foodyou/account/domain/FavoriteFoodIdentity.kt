package com.maksimowiczm.foodyou.account.domain

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FavoriteFoodIdentity {
    @Serializable data class FoodDataCentral(val fdcId: Int) : FavoriteFoodIdentity

    @Serializable data class OpenFoodFacts(val barcode: String) : FavoriteFoodIdentity

    @Serializable data class UserProduct(val id: Uuid) : FavoriteFoodIdentity

    @Serializable data class Recipe(val id: Uuid) : FavoriteFoodIdentity
}
