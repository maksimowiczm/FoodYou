package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain

import com.maksimowiczm.foodyou.feature.diary.core.data.search.SearchQuery
import com.maksimowiczm.foodyou.feature.diary.core.data.search.SearchRepository
import kotlinx.coroutines.flow.Flow

internal fun interface ObserveRecentQueriesUseCase {
    operator fun invoke(limit: Int): Flow<List<SearchQuery>>
}

internal class ObserveRecentQueriesUseCaseImpl(private val searchRepository: SearchRepository) :
    ObserveRecentQueriesUseCase {
    override fun invoke(limit: Int): Flow<List<SearchQuery>> =
        searchRepository.observeRecentQueries(limit)
}
