package com.maksimowiczm.foodyou.food.search.infrastructure

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.food.infrastructure.usda.FoodDataCentralRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import kotlinx.coroutines.flow.Flow

class SearchableFoodRepositoryImpl(
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
) : SearchableFoodRepository {
    override fun search(
        parameters: SearchParameters,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> =
        when (parameters) {
            is SearchParameters.FoodDataCentral ->
                foodDataCentralRepository.search(parameters, pageSize)
        }

    override fun count(parameters: SearchParameters): Flow<Int> =
        when (parameters) {
            is SearchParameters.FoodDataCentral -> foodDataCentralRepository.count(parameters)
        }
}
