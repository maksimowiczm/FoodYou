package com.maksimowiczm.foodyou.food.search.infrastructure

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodRepository
import kotlinx.coroutines.flow.Flow

class SearchableFoodRepositoryImpl(private val openFoodFactsRepository: OpenFoodFactsRepository) :
    SearchableFoodRepository {
    override fun search(
        parameters: SearchParameters,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> =
        when (parameters) {
            is SearchParameters.FoodDataCentral -> TODO()
            is SearchParameters.Local -> TODO()
            is SearchParameters.OpenFoodFacts ->
                openFoodFactsRepository.search(parameters, pageSize)
        }
}
