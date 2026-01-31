package com.maksimowiczm.foodyou.app.ui.food.search

import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.FoodIdentity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveFavoriteFoodUseCase(
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val userFoodRepository: UserFoodRepository,
    private val nameSelector: FoodNameSelector,
) {
    fun observe(
        favoriteFoods: List<FavoriteFoodIdentity>,
        query: SearchQuery,
        accountId: LocalAccountId,
    ): Flow<List<FoodSearchUiModel>> {
        val foods =
            favoriteFoods
                .map { id ->
                    when (id) {
                        is FavoriteFoodIdentity.FoodDataCentral ->
                            foodDataCentralRepository
                                .observe(FoodDataCentralProductIdentity(id.fdcId))
                                .map {
                                    when (it) {
                                        is RemoteData.Error ->
                                            FoodSearchUiModel.Error(
                                                identity = FoodIdentity.from(id),
                                                name = null,
                                                brand = null,
                                                error = it.error,
                                            )

                                        is RemoteData.Success<FoodDataCentralProduct> ->
                                            FoodSearchUiModel.Loaded(it.value)

                                        is RemoteData.Loading ->
                                            FoodSearchUiModel.Loading(FoodIdentity.from(id))

                                        is RemoteData.NotFound ->
                                            FoodSearchUiModel.Error(
                                                identity = FoodIdentity.from(id),
                                                name = null,
                                                brand = null,
                                                error = Exception("Product not found"),
                                            )
                                    }
                                }

                        is FavoriteFoodIdentity.OpenFoodFacts ->
                            openFoodFactsRepository
                                .observe(OpenFoodFactsProductIdentity(id.barcode))
                                .map {
                                    when (it) {
                                        is RemoteData.Error ->
                                            FoodSearchUiModel.Error(
                                                identity = FoodIdentity.from(id),
                                                name = null,
                                                brand = null,
                                                error = it.error,
                                            )

                                        is RemoteData.Success<OpenFoodFactsProduct> ->
                                            FoodSearchUiModel.Loaded(it.value)

                                        is RemoteData.Loading ->
                                            FoodSearchUiModel.Loading(FoodIdentity.from(id))

                                        is RemoteData.NotFound ->
                                            FoodSearchUiModel.Error(
                                                identity = FoodIdentity.from(id),
                                                name = null,
                                                brand = null,
                                                error = Exception("Product not found"),
                                            )
                                    }
                                }

                        is FavoriteFoodIdentity.UserFoodProduct ->
                            userFoodRepository
                                .observe(UserFoodProductIdentity(id.id, accountId))
                                .map {
                                    when (it) {
                                        null ->
                                            FoodSearchUiModel.Loading(
                                                FoodIdentity.from(id, accountId)
                                            )

                                        else -> FoodSearchUiModel.Loaded(it)
                                    }
                                }
                    }
                }
                .combine()

        return foods.map { list ->
            val list =
                when (query) {
                    SearchQuery.Blank -> list
                    is SearchQuery.Barcode ->
                        list.filter {
                            when (it) {
                                is FoodSearchUiModel.Loaded ->
                                    it.barcode?.value?.contains(query.barcode) ?: false

                                is FoodSearchUiModel.Loading -> true
                                is FoodSearchUiModel.Error -> false
                            }
                        }

                    is SearchQuery.FoodDataCentralUrl ->
                        list.filter {
                            val identity = it.identity
                            identity is FoodIdentity.FoodDataCentral &&
                                identity.identity.fdcId == query.fdcId
                        }

                    is SearchQuery.OpenFoodFactsUrl ->
                        list.filter {
                            val identity = it.identity
                            identity is FoodIdentity.OpenFoodFacts &&
                                identity.identity.barcode == query.barcode
                        }

                    is SearchQuery.Text ->
                        list.filter {
                            when (it) {
                                is FoodSearchUiModel.Loaded -> it.name.contains(query.query)
                                is FoodSearchUiModel.Error ->
                                    it.name?.contains(query.query) ?: false

                                is FoodSearchUiModel.Loading -> true
                            }
                        }
                }

            list.sortedWith(FoodSearchUiModel.comparator(nameSelector))
        }
    }
}
