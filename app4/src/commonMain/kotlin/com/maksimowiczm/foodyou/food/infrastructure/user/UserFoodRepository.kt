package com.maksimowiczm.foodyou.food.infrastructure.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDao
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class UserFoodRepository(private val dao: UserFoodDao, private val nameSelector: FoodNameSelector) {
    private val mapper = UserFoodMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        searchFoodParams: SearchParameters.User,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val language = nameSelector.select()

        when (searchFoodParams.query) {
            SearchQuery.Blank,
            is SearchQuery.Barcode,
            is SearchQuery.Text -> Unit

            is SearchQuery.FoodDataCentralUrl,
            is SearchQuery.OpenFoodFactsUrl -> {
                return flowOf(
                    PagingData.empty(
                        sourceLoadStates =
                            LoadStates(NotLoading(true), NotLoading(true), NotLoading(true))
                    )
                )
            }
        }

        val config = PagingConfig(pageSize = pageSize)
        val factory = {
            when (searchFoodParams.query) {
                SearchQuery.Blank ->
                    dao.getPagingSource(language.tag, searchFoodParams.accountId.value)

                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(
                        searchFoodParams.query.barcode,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(
                        searchFoodParams.query.query,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::searchableFoodDto)
        }
    }

    fun count(parameters: SearchParameters.User): Flow<Int> {
        val countFlow: Flow<Int> =
            when (parameters.query) {
                SearchQuery.Blank -> dao.observeCount(parameters.accountId.value)

                is SearchQuery.Barcode ->
                    dao.observeCountByBarcode(parameters.query.barcode, parameters.accountId.value)

                is SearchQuery.Text ->
                    dao.observeCountByQuery(parameters.query.query, parameters.accountId.value)

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> flowOf(0)
            }

        return countFlow
    }

    fun observe(queryParameters: QueryParameters.Local): Flow<FoodProductRepository.FoodStatus> {
        val (id, accountId, _) = queryParameters

        return dao.observe(id.id, accountId.value)
            .map { entity -> entity?.let(mapper::foodProductDto) }
            .map {
                when (it) {
                    null -> FoodProductRepository.FoodStatus.NotFound
                    else -> FoodProductRepository.FoodStatus.Available(it)
                }
            }
    }
}
