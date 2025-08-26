package com.maksimowiczm.foodyou.feature.shared.usecase

import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import kotlinx.coroutines.flow.Flow

fun interface ObserveSettingsUseCase {
    fun observe(): Flow<Settings>
}

internal class ObserveSettingsUseCaseImpl(private val queryBus: QueryBus) : ObserveSettingsUseCase {
    override fun observe(): Flow<Settings> = queryBus.dispatch(ObserveSettingsQuery)
}
