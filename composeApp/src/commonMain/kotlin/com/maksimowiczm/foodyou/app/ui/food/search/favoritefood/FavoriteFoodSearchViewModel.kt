package com.maksimowiczm.foodyou.app.ui.food.search.favoritefood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FavoriteFoodSearchViewModel(
    appProfileManager: AppProfileManager,
    private val foodDataCentralService: FoodDataCentralService,
    private val openFoodFactsService: OpenFoodFactsService,
    private val userProductService: UserProductService,
    private val nameSelector: FoodNameSelector,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val favoriteFoodIdentities =
        appProfileManager.observeAppProfile().map { it.favoriteFoods }

    private val foodList: Flow<List<RemoteData<Any>>> =
        favoriteFoodIdentities
            .map { list ->
                if (list.isEmpty()) return@map flowOf(listOf())

                list
                    .map { identity ->
                        when (identity) {
                            is FavoriteFoodIdentity.FoodDataCentral ->
                                foodDataCentralService.observe(
                                    FoodDataCentralProductIdentity(identity.fdcId)
                                )

                            is FavoriteFoodIdentity.OpenFoodFacts ->
                                openFoodFactsService.observe(
                                    OpenFoodFactsProductIdentity(identity.barcode)
                                )

                            is FavoriteFoodIdentity.UserProduct ->
                                userProductService.observe(UserProductIdentity(identity.id)).map {
                                    food ->
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

                    is FoodDataCentralUrlSearchQuery ->
                        list.filterIsInstance<RemoteData.Success<FoodDataCentralProduct>>().filter {
                            it.value.identity.fdcId == query.fdcId
                        }

                    is OpenFoodFactsUrlSearchQuery ->
                        list.filterIsInstance<RemoteData.Success<OpenFoodFactsProduct>>().filter {
                            it.value.identity.barcode == query.barcode
                        }

                    is SearchQuery.NotBlank ->
                        list.filter { it.name()?.contains(query.query) ?: false }
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
        is FoodDataCentralProduct -> FoodName(english = name, fallback = name)
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
        is UserProduct -> brand
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
