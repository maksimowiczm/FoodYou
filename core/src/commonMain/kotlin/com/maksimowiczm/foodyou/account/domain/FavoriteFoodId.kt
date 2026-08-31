package com.maksimowiczm.foodyou.account.domain

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed interface FavoriteFoodId {
    @Serializable data class FoodDataCentral(val fdcId: Int) : FavoriteFoodId

    @Serializable data class OpenFoodFacts(val barcode: String) : FavoriteFoodId

    @Serializable data class UserProduct(val id: Uuid) : FavoriteFoodId

    @Serializable data class Recipe(val id: Uuid) : FavoriteFoodId
}
