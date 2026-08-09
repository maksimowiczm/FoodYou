package com.maksimowiczm.foodyou.app.application

import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveIsFavoriteFoodUseCase(private val appProfileManager: AppProfileManager) {
    fun observe(identity: OpenFoodFactsProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.OpenFoodFacts(identity.barcode))

    fun observe(identity: FoodDataCentralProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.FoodDataCentral(identity.fdcId))

    fun observe(identity: UserProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.UserProduct(identity.id))

    fun observe(identity: UserRecipeIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.Recipe(identity.id))

    fun observe(identity: FavoriteFoodIdentity): Flow<Boolean> =
        appProfileManager.observeAppProfile().map { it.favoriteFoods.contains(identity) }
}
