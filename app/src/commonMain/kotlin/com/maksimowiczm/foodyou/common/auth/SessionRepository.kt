package com.maksimowiczm.foodyou.common.auth

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun saveSession(session: Session)

    fun observeSession(): Flow<Session?>

    suspend fun clearSession()
}
