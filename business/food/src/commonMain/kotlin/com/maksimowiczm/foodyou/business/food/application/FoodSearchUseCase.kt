package com.maksimowiczm.foodyou.business.food.application

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchDomainEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.food.domain.remote.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.domain.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

interface FoodSearchUseCase {
    fun search(
        query: String?,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>>

    fun searchRecent(query: String?, excludedRecipeId: FoodId.Recipe?): Flow<PagingData<FoodSearch>>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class FoodSearchUseCaseImpl(
    private val foodSearchRepository: FoodSearchRepository,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
    private val openFoodFactsRemoteMediatorFactory: ProductRemoteMediatorFactory,
    private val usdaRemoteMediatorFactory: ProductRemoteMediatorFactory,
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

        return foodSearchPreferencesRepository.observe().flatMapLatest { prefs ->
            foodSearchRepository.search(
                query = queryType,
                source = source,
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediatorFactory = prefs.remoteMediatorFactory(source)?.wrap(queryType),
                excludedRecipeId = excludedRecipeId,
            )
        }
    }

    override fun searchRecent(
        query: String?,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> {
        val queryType = queryType(query)

        if (queryType is QueryType.NotBlank.Text) {
            eventBus.publish(
                FoodSearchDomainEvent(queryType = queryType, date = dateProvider.now())
            )
        }

        return foodSearchRepository.searchRecent(
            query = queryType,
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
                openFoodFactsRemoteMediatorFactory

            FoodSource.Type.USDA if this.usda.enabled -> usdaRemoteMediatorFactory
            else -> null
        }

    @OptIn(ExperimentalPagingApi::class)
    private fun ProductRemoteMediatorFactory.wrap(queryType: QueryType): RemoteMediatorFactory =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? = runBlocking {
                this@wrap.create(query = queryType, pageSize = PAGE_SIZE)
            }
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
