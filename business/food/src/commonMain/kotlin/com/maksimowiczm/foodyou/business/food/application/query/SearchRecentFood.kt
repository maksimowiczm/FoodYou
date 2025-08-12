package com.maksimowiczm.foodyou.business.food.application.query

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.food.application.event.FoodSearchEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data class SearchRecentFoodQuery(val query: String?, val excludedRecipeId: FoodId.Recipe?) :
    Query<PagingData<FoodSearch>>

internal class SearchRecentFoodQueryHandler(
    private val eventBus: EventBus,
    private val foodSearchSource: LocalFoodSearchDataSource,
    private val dateProvider: DateProvider,
) : QueryHandler<SearchRecentFoodQuery, PagingData<FoodSearch>> {

    override fun handle(query: SearchRecentFoodQuery): Flow<PagingData<FoodSearch>> {
        val (queryText, excludedRecipeId) = query
        val queryType = queryType(queryText)

        if (queryType is QueryType.NotBlank.Text) {
            eventBus.publish(FoodSearchEvent(queryType = queryType, date = dateProvider.now()))
        }

        return foodSearchSource.searchRecent(
            query = queryType,
            config = PagingConfig(PAGE_SIZE),
            excludedRecipeId = excludedRecipeId?.id,
        )
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
