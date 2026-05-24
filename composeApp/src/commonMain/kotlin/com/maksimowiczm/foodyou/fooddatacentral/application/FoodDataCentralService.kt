package com.maksimowiczm.foodyou.fooddatacentral.application

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.onSuccess
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class FoodDataCentralService(
    private val repository: FoodDataCentralRepository,
    private val settingsRepository: FoodDataCentralSettingsRepository,
    private val eventBus: EventBus,
    private val clock: Clock = Clock.System,
) {
    fun search(
        parameters: FoodDataCentralSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<FoodDataCentralProduct>> {
        return settingsRepository.observe().flatMapLatest { settings ->
            repository.search(
                parameters = parameters,
                pageSize = pageSize,
                remoteEnabled = settings.remoteEnabled,
                apiKey = settings.apiKey,
            )
        }
    }

    fun count(parameters: FoodDataCentralSearchParameters): Flow<Int> {
        return repository.count(parameters)
    }

    fun observe(
        identity: FoodDataCentralProductIdentity
    ): Flow<RemoteData<FoodDataCentralProduct>> {
        return settingsRepository.observe().distinctUntilChanged().flatMapLatest { settings ->
            repository.observe(
                identity = identity,
                remoteEnabled = settings.remoteEnabled,
                apiKey = settings.apiKey,
            )
        }
    }

    suspend fun refresh(
        identity: FoodDataCentralProductIdentity
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError> {
        val apiKey = settingsRepository.observe().map { it.apiKey }.first()
        return repository.refresh(identity, apiKey).onSuccess {
            eventBus.publish(
                FoodDataCentralProductUpdatedEvent(product = it, timestamp = clock.now())
            )
        }
    }
}
