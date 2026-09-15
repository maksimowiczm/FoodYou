package com.maksimowiczm.foodyou.preferences.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserPreferencesRepository {
    fun observe(key: String): Flow<UserPreferences>

    suspend fun update(key: String, transform: (UserPreferences) -> UserPreferences)

    companion object
}

inline fun <reified T : UserPreferences> UserPreferencesRepository.observe() =
    observe(key = T::class.qualifiedName!!).map { it as T }

suspend inline fun <reified T : UserPreferences> UserPreferencesRepository.update(
    noinline transform: (T) -> T
) = update(key = T::class.qualifiedName!!) { transform(it as T) }
