package com.maksimowiczm.foodyou.app.infrastructure.opensource.settings

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepositoryOf
import org.koin.core.module.Module

internal fun Module.settingsModule() {
    userPreferencesRepositoryOf(::DataStoreSettingsRepository)
}
