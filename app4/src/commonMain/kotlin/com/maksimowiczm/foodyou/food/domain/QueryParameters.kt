package com.maksimowiczm.foodyou.food.domain

sealed interface QueryParameters {
    data class Local(val identity: FoodProductIdentity.Local) : QueryParameters

    data class OpenFoodFacts(val identity: FoodProductIdentity.OpenFoodFacts) : QueryParameters

    data class FoodDataCentral(val identity: FoodProductIdentity.FoodDataCentral) : QueryParameters
}
