package com.maksimowiczm.foodyou.preferences.infrastructure

import com.maksimowiczm.foodyou.preferences.domain.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferenceProvider<T : UserPreferences> {
    val key: String

    fun observe(): Flow<T>

    suspend fun update(transform: (T) -> T)
}

@Suppress("UNCHECKED_CAST")
suspend fun <T : UserPreferences> UserPreferenceProvider<T>.update(
    transform: (UserPreferences) -> UserPreferences
) = update { transform(it) as T }
