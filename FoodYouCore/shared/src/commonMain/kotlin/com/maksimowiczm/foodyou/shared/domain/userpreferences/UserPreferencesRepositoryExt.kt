package com.maksimowiczm.foodyou.shared.domain.userpreferences

import kotlinx.coroutines.flow.first

suspend fun <P : UserPreferences> UserPreferencesRepository<P>.get(): P = observe().first()
