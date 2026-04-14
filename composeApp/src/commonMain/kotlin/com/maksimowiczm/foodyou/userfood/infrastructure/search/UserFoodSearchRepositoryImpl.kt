package com.maksimowiczm.foodyou.userfood.infrastructure.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchItem
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchParameters
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductMapper
import com.maksimowiczm.foodyou.userfood.infrastructure.room.search.SearchDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class UserFoodSearchRepositoryImpl(private val dao: SearchDao) : UserFoodSearchRepository {
    private val mapper = UserFoodSearchMapper(ProductMapper())

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: UserFoodSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<UserFoodSearchItem>> {
        when (parameters.query) {
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
            when (parameters.query) {
                SearchQuery.Blank -> dao.getPagingSource(parameters.language.tag)

                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(parameters.query.barcode, parameters.language.tag)

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(parameters.query.query, parameters.language.tag)

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::userFoodSearchItem)
        }
    }

    override fun count(parameters: UserFoodSearchParameters): Flow<Int> {
        val countFlow: Flow<Int> =
            when (parameters.query) {
                SearchQuery.Blank -> dao.observeCount()
                is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
                is SearchQuery.Text -> dao.observeCountByQuery(parameters.query.query)
                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> flowOf(0)
            }

        return countFlow
    }
}
