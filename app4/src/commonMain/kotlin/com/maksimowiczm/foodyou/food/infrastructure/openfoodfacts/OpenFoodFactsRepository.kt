package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDao
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class OpenFoodFactsRepository(
    private val networkDataSource: OpenFoodFactsRemoteDataSource,
    private val searchPreferencesRepository: FoodSearchPreferencesRepository,
    private val database: OpenFoodFactsDatabase,
    private val dao: OpenFoodFactsDao,
    private val logger: Logger,
) {
    private val mapper = OpenFoodFactsProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        searchFoodParams: SearchParameters.OpenFoodFacts,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val config = PagingConfig(pageSize = pageSize)

        val factory = {
            when (searchFoodParams.query) {
                SearchQuery.Blank -> dao.getPagingSource()
                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(searchFoodParams.query.barcode)

                is SearchQuery.Text -> dao.getPagingSourceByQuery(searchFoodParams.query.query)
            }
        }

        return searchPreferencesRepository.observe().flatMapLatest { prefs ->
            val remoteMediator =
                if (prefs.allowOpenFoodFacts && searchFoodParams.query is SearchQuery.NotBlank) {
                    OpenFoodFactsRemoteMediator(
                        query = searchFoodParams.query,
                        database = database,
                        remote = networkDataSource,
                        logger = logger,
                    )
                } else null

            Pager(config = config, pagingSourceFactory = factory, remoteMediator = remoteMediator)
                .flow
                .map { data -> data.map(mapper::map) }
        }
    }
}
