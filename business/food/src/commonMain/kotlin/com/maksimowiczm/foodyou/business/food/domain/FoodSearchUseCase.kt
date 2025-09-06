package com.maksimowiczm.foodyou.business.food.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.date.DateProvider
import com.maksimowiczm.foodyou.shared.event.EventBus
import com.maksimowiczm.foodyou.shared.food.FoodSource
import com.maksimowiczm.foodyou.shared.search.SearchQuery
import com.maksimowiczm.foodyou.shared.search.searchQuery
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class FoodSearchUseCase(
    private val foodSearchRepository: FoodSearchRepository,
    private val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
    private val foodRemoteMediatorFactoryAggregate: FoodRemoteMediatorFactoryAggregate,
    private val eventBus: EventBus,
    private val dateProvider: DateProvider,
) {
    fun search(
        query: String?,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> {
        val query = searchQuery(query)

        if (query is SearchQuery.Text) {
            eventBus.publish(FoodSearchEvent(query, dateProvider.now()))
        }

        return foodSearchPreferencesRepository.observe().flatMapLatest { prefs ->
            foodSearchRepository.search(
                query = query,
                source = source,
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediatorFactory = prefs.remoteMediatorFactory(source)?.wrap(query),
                excludedRecipeId = excludedRecipeId,
            )
        }
    }

    fun searchRecent(
        query: String?,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> {
        val query = searchQuery(query)

        if (query is SearchQuery.Text) {
            eventBus.publish(FoodSearchEvent(query, dateProvider.now()))
        }

        return foodSearchRepository.searchRecent(
            query = query,
            config = PagingConfig(pageSize = PAGE_SIZE),
            now = dateProvider.now(),
            excludedRecipeId = excludedRecipeId,
        )
    }

    private fun FoodSearchPreferences.remoteMediatorFactory(
        source: FoodSource.Type
    ): ProductRemoteMediatorFactory? =
        when (source) {
            FoodSource.Type.OpenFoodFacts if this.openFoodFacts.enabled ->
                foodRemoteMediatorFactoryAggregate.openFoodFactsRemoteMediatorFactory

            FoodSource.Type.USDA if this.usda.enabled ->
                foodRemoteMediatorFactoryAggregate.usdaRemoteMediatorFactory
            else -> null
        }

    @OptIn(ExperimentalPagingApi::class)
    private fun ProductRemoteMediatorFactory.wrap(query: SearchQuery): RemoteMediatorFactory =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? = runBlocking {
                this@wrap.create(query, PAGE_SIZE)
            }
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
