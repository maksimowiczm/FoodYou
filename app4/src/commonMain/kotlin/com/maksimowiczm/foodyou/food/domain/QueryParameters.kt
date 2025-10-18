package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId

sealed interface QueryParameters {
    data class Local(
        val identity: FoodProductIdentity.Local,
        val accountId: LocalAccountId,
        val profileId: ProfileId,
    ) : QueryParameters

    data class OpenFoodFacts(val identity: FoodProductIdentity.OpenFoodFacts) : QueryParameters

    data class FoodDataCentral(val identity: FoodProductIdentity.FoodDataCentral) : QueryParameters
}
