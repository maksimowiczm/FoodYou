package com.maksimowiczm.foodyou.food.search.infrastructure

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchableFoodRepositoryImpl(private val openFoodFactsRepository: OpenFoodFactsRepository) :
    SearchableFoodRepository {
    override fun search(
        parameters: SearchParameters,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> =
        when (parameters) {
            is SearchParameters.FoodDataCentral -> flowOf(PagingData.empty())
            is SearchParameters.Local -> flowOf(PagingData.empty())
            is SearchParameters.OpenFoodFacts ->
                openFoodFactsRepository.search(parameters, pageSize)
        }

    override fun count(parameters: SearchParameters): Flow<Int> =
        when (parameters) {
            is SearchParameters.FoodDataCentral -> flowOf(0)
            is SearchParameters.Local -> flowOf(0)
            is SearchParameters.OpenFoodFacts -> openFoodFactsRepository.count(parameters)
        }
}
