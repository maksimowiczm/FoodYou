package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class ObserveIsFavoriteFoodUseCase(
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val accountManager: AccountManager,
) {
    fun observe(identity: OpenFoodFactsProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.OpenFoodFacts(identity.barcode))

    fun observe(identity: FoodDataCentralProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.FoodDataCentral(identity.fdcId))

    fun observe(identity: UserProductIdentity): Flow<Boolean> =
        observe(FavoriteFoodIdentity.UserProduct(identity.id))

    fun observe(identity: FavoriteFoodIdentity): Flow<Boolean> =
        combine(observePrimaryAccountUseCase.observe(), accountManager.observePrimaryProfileId()) {
                account,
                profileId ->
                account.profiles.single { it.id == profileId }
            }
            .map { profile -> profile.isFavorite(identity) }
}
