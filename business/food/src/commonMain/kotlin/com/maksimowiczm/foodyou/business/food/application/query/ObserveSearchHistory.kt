package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveSearchHistoryQuery : Query<List<SearchHistory>>

internal class ObserveSearchHistoryQueryHandler(
    private val localFoodSearch: LocalFoodSearchDataSource
) : QueryHandler<ObserveSearchHistoryQuery, List<SearchHistory>> {

    override fun handle(query: ObserveSearchHistoryQuery): Flow<List<SearchHistory>> =
        localFoodSearch.observeSearchHistory(limit = 10)
}
