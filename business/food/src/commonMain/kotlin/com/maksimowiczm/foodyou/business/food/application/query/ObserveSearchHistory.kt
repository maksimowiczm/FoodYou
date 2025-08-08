package com.maksimowiczm.foodyou.business.food.application.query

import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data object ObserveSearchHistoryQuery : Query

internal class ObserveSearchHistoryQueryHandler(
    private val localFoodSearch: LocalFoodSearchDataSource
) : QueryHandler<ObserveSearchHistoryQuery, List<SearchHistory>> {
    override val queryType: KClass<ObserveSearchHistoryQuery>
        get() = ObserveSearchHistoryQuery::class

    override fun handle(query: ObserveSearchHistoryQuery): Flow<List<SearchHistory>> =
        localFoodSearch.observeSearchHistory(limit = 10)
}
