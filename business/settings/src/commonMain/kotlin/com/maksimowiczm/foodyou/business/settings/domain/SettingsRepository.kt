package com.maksimowiczm.foodyou.business.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observe(): Flow<Settings>

    suspend fun update(transform: Settings.() -> Settings)
}
