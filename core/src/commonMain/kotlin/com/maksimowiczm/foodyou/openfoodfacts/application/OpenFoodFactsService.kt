package com.maksimowiczm.foodyou.openfoodfacts.application

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.onSuccess
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class OpenFoodFactsService(
    private val repository: OpenFoodFactsRepository,
    private val settingsRepository: OpenFoodFactsSettingsRepository,
    private val eventNotifier: EventNotifier,
) {
    fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<OpenFoodFactsProduct>> {
        return settingsRepository.observe().flatMapLatest { settings ->
            repository.search(
                parameters = parameters,
                pageSize = pageSize,
                remoteEnabled = settings.remoteEnabled,
                credentials = settings.credentials?.decrypt(),
                onNewProduct = { products ->
                    if (products.isNotEmpty()) {
                        eventNotifier.notify(
                            products.map {
                                OpenFoodFactsProductUpdatedEvent(product = it)
                            }
                        )
                    }
                },
            )
        }
    }

    fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int> {
        return repository.count(parameters)
    }

    fun observe(id: OpenFoodFactsProductId): Flow<RemoteData<OpenFoodFactsProduct>> {
        return settingsRepository
            .observe()
            .map { it.remoteEnabled }
            .distinctUntilChanged()
            .flatMapLatest { remoteEnabled ->
                repository.observe(id = id, remoteEnabled = remoteEnabled)
            }
    }

    suspend fun refresh(
        id: OpenFoodFactsProductId
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError> {
        return repository.refresh(id).onSuccess {
            eventNotifier.notify(OpenFoodFactsProductUpdatedEvent(product = it))
        }
    }
}
