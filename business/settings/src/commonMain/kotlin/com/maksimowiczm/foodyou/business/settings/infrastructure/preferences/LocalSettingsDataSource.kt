package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import kotlinx.coroutines.flow.Flow

internal interface LocalSettingsDataSource {
    fun observe(): Flow<Settings>

    suspend fun update(settings: Settings)
}
