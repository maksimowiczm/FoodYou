package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteMediator
import kotlinx.coroutines.flow.Flow

class ObserveOpenFoodFactsProductPagesUseCase(
    database: OpenFoodFactsDatabase,
    private val remoteDataSource: OpenFoodFactsRemoteDataSource
) {
    private val dao = database.openFoodFactsDao

    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        query: String? = null,
        pageSize: Int = 30
    ): Flow<PagingData<OpenFoodFactsProduct>> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        val mediator = if (!query.isNullOrBlank()) {
            OpenFoodFactsRemoteMediator(
                remoteDataSource = remoteDataSource,
                dao = dao,
                query = query,
                country = null,
                isBarcode = isBarcode
            )
        } else {
            null
        }

        val pagingSourceFactory = if (isBarcode) {
            { dao.observeProductsByBarcode(query) }
        } else {
            {
                // Split queries into 3 words
                val words = query?.split(" ") ?: emptyList()
                val query1 = words.getOrNull(0)
                val query2 = words.getOrNull(1)
                val query3 = if (words.size > 2) {
                    words.drop(2).joinToString(" ")
                } else {
                    null
                }

                dao.observeProducts(
                    query1 = query1,
                    query2 = query2,
                    query3 = query3
                )
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = pageSize
            ),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = mediator
        ).flow
    }
}
