package com.maksimowiczm.foodyou.common.domain.userpreferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository<P : UserPreferences> {
    fun observe(): Flow<P>

    suspend fun update(transform: P.() -> P)
}
