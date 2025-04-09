package com.maksimowiczm.foodyou.feature.addfood.domain

import com.maksimowiczm.foodyou.core.model.SearchQuery
import com.maksimowiczm.foodyou.core.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

internal fun interface ObserveRecentQueriesUseCase {
    operator fun invoke(limit: Int): Flow<List<SearchQuery>>
}

internal class ObserveRecentQueriesUseCaseImpl(private val searchRepository: SearchRepository) :
    ObserveRecentQueriesUseCase {
    override fun invoke(limit: Int): Flow<List<SearchQuery>> =
        searchRepository.observeRecentQueries(limit)
}
