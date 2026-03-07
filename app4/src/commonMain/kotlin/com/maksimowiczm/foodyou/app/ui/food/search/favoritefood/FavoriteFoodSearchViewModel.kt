package com.maksimowiczm.foodyou.app.ui.food.search.favoritefood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FavoriteFoodSearchViewModel(
    private val appAccountManager: AppAccountManager,
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val userProductRepository: UserProductRepository,
    private val nameSelector: FoodNameSelector,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val favoriteFoodIdentities =
        appAccountManager.observeAppProfile().map { it.favoriteFoods }

    private val accountId = appAccountManager.observeAppAccountId().filterNotNull()

    private val foodList: Flow<List<RemoteData<Any>>> =
        combine(favoriteFoodIdentities, accountId) { list, accountId ->
                if (list.isEmpty()) return@combine flowOf(listOf())

                list
                    .map { identity ->
                        when (identity) {
                            is FavoriteFoodIdentity.FoodDataCentral ->
                                foodDataCentralRepository.observe(
                                    FoodDataCentralProductIdentity(identity.fdcId)
                                )

                            is FavoriteFoodIdentity.OpenFoodFacts ->
                                openFoodFactsRepository.observe(
                                    OpenFoodFactsProductIdentity(identity.barcode)
                                )

                            is FavoriteFoodIdentity.UserProduct ->
                                userProductRepository
                                    .observe(UserProductIdentity(identity.id, accountId))
                                    .map { food ->
                                        when (food) {
                                            null -> RemoteData.NotFound
                                            else -> RemoteData.Success(food)
                                        }
                                    }
                        }
                    }
                    .combine()
            }
            .flatMapLatest { it }
            .combine(searchQuery) { list, query ->
                when (query) {
                    SearchQuery.Blank -> list
                    is SearchQuery.Barcode ->
                        list.filter { it.barcode()?.contains(query.barcode) ?: false }

                    is SearchQuery.FoodDataCentralUrl ->
                        list.filterIsInstance<RemoteData.Success<FoodDataCentralProduct>>().filter {
                            it.value.identity.fdcId == query.fdcId
                        }

                    is SearchQuery.OpenFoodFactsUrl ->
                        list.filterIsInstance<RemoteData.Success<OpenFoodFactsProduct>>().filter {
                            it.value.identity.barcode == query.barcode
                        }

                    is SearchQuery.Text -> list.filter { it.name()?.contains(query.query) ?: false }
                }.sortedWith { a, b ->
                    val nameA =
                        a.name()?.let(nameSelector::select) ?: return@sortedWith Int.MAX_VALUE
                    val nameB =
                        b.name()?.let(nameSelector::select) ?: return@sortedWith Int.MIN_VALUE

                    val brandA = a.brand()
                    val brandB = b.brand()

                    val result = nameA.compareTo(nameB, ignoreCase = true)
                    if (result == 0 && brandA != null && brandB != null)
                        brandA.compareTo(brandB, ignoreCase = true)
                    else result
                }
            }

    val pages =
        foodList
            .map { list ->
                val loadStates =
                    LoadStates(
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true),
                    )

                if (list.isEmpty()) {
                    return@map PagingData.empty(
                        mediatorLoadStates = loadStates,
                        sourceLoadStates = loadStates,
                    )
                }

                PagingData.from(
                    data = list,
                    sourceLoadStates = loadStates,
                    mediatorLoadStates = loadStates,
                )
            }
            .cachedIn(viewModelScope)

    val count =
        foodList
            .map { list -> list.count { it is RemoteData.Success } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }
}

private fun Any.barcode(): String? =
    when (this) {
        is UserProduct -> barcode?.value
        is OpenFoodFactsProduct -> identity.barcode
        is FoodDataCentralProduct -> barcode
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.barcode(): String? =
    when (this) {
        is RemoteData.Success -> value.barcode()
        is RemoteData.Error -> partialValue?.barcode()
        is RemoteData.Loading -> partialValue?.barcode()
        is RemoteData.NotFound -> null
    }

private fun Any.name(): FoodName =
    when (this) {
        is UserProduct -> name
        is OpenFoodFactsProduct -> name
        is FoodDataCentralProduct -> name
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.name(): FoodName? =
    when (this) {
        is RemoteData.Success -> value.name()
        is RemoteData.Error -> partialValue?.name()
        is RemoteData.Loading -> partialValue?.name()
        is RemoteData.NotFound -> null
    }

private fun Any.brand(): String? =
    when (this) {
        is UserProduct -> brand?.value
        is OpenFoodFactsProduct -> brand
        is FoodDataCentralProduct -> brand
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.brand(): String? =
    when (this) {
        is RemoteData.Success -> value.brand()
        is RemoteData.Error -> partialValue?.brand()
        is RemoteData.Loading -> partialValue?.brand()
        is RemoteData.NotFound -> null
    }
