package com.maksimowiczm.foodyou.business.food.application

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchDomainEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow

fun interface FoodSearchUseCase {
    fun search(
        query: String?,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>
}

internal class FoodSearchUseCaseImpl(
    private val foodSearchRepository: FoodSearchRepository,
    private val dateProvider: DateProvider,
    private val eventBus: EventBus,
) : FoodSearchUseCase {
    override fun search(
        query: String?,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> {
        val queryType = queryType(query)

        if (queryType is QueryType.NotBlank.Text) {
            eventBus.publish(
                FoodSearchDomainEvent(queryType = queryType, date = dateProvider.now())
            )
        }

        return foodSearchRepository.searchFood(
            query = queryType,
            source = source,
            excludedRecipeId = excludedRecipeId,
        )
    }
}
