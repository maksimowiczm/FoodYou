package com.maksimowiczm.foodyou.food.search.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.event.EventBus
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.searchQuery
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

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
            eventBus.publish(FoodSearchEvent(query, dateProvider.nowInstant()))
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
            eventBus.publish(FoodSearchEvent(query, dateProvider.nowInstant()))
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
    ): Pair<ProductRemoteMediatorFactory, DietaryFilter?>? =
        when (source) {
            FoodSource.Type.OpenFoodFacts if this.openFoodFacts.enabled ->
                foodRemoteMediatorFactoryAggregate.openFoodFactsRemoteMediatorFactory to
                    this.openFoodFacts.dietaryFilter

            FoodSource.Type.USDA if this.usda.enabled ->
                foodRemoteMediatorFactoryAggregate.usdaRemoteMediatorFactory to null
            else -> null
        }

    @OptIn(ExperimentalPagingApi::class)
    private fun Pair<ProductRemoteMediatorFactory, DietaryFilter?>.wrap(
        query: SearchQuery,
    ): RemoteMediatorFactory {
        val (factory, dietaryFilter) = this
        return object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? = runBlocking {
                factory.create(query, PAGE_SIZE, dietaryFilter)
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
