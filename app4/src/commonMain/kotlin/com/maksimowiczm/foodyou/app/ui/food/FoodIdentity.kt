package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity

@Immutable
sealed interface FoodIdentity {

    @Immutable data class OpenFoodFacts(val identity: OpenFoodFactsProductIdentity) : FoodIdentity

    @Immutable data class UserFood(val identity: UserFoodProductIdentity) : FoodIdentity

    @Immutable
    data class FoodDataCentral(val identity: FoodDataCentralProductIdentity) : FoodIdentity

    companion object {
        fun from(identity: FavoriteFoodIdentity.OpenFoodFacts) =
            OpenFoodFacts(OpenFoodFactsProductIdentity(identity.barcode))

        fun from(identity: FavoriteFoodIdentity.UserFoodProduct, localAccountId: LocalAccountId) =
            UserFood(UserFoodProductIdentity(identity.id, localAccountId))

        fun from(identity: FavoriteFoodIdentity.FoodDataCentral) =
            FoodDataCentral(FoodDataCentralProductIdentity(identity.fdcId))
    }
}
