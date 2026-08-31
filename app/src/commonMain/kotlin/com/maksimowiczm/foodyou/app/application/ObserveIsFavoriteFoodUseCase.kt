package com.maksimowiczm.foodyou.app.application

import com.maksimowiczm.foodyou.account.domain.FavoriteFoodId
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveIsFavoriteFoodUseCase(private val appProfileManager: AppProfileManager) {
    fun observe(id: OpenFoodFactsProductId): Flow<Boolean> =
        observe(FavoriteFoodId.OpenFoodFacts(id.barcode))

    fun observe(id: FoodDataCentralProductId): Flow<Boolean> =
        observe(FavoriteFoodId.FoodDataCentral(id.fdcId))

    fun observe(id: UserProductId): Flow<Boolean> = observe(FavoriteFoodId.UserProduct(id.value))

    fun observe(id: UserRecipeId): Flow<Boolean> = observe(FavoriteFoodId.Recipe(id.value))

    fun observe(id: FavoriteFoodId): Flow<Boolean> =
        appProfileManager.observeAppProfile().map { it.favoriteFoods.contains(id) }
}
