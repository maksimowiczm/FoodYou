package com.maksimowiczm.foodyou.preferences.infrastructure

import com.maksimowiczm.foodyou.preferences.domain.UserPreferences
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl(vararg providers: UserPreferenceProvider<*>) :
    UserPreferencesRepository {
    private val map = providers.associateBy { it.key }

    override fun observe(key: String): Flow<UserPreferences> {
        val storage = checkNotNull(map[key])
        return storage.observe()
    }

    override suspend fun update(
        key: String,
        transform: (UserPreferences) -> UserPreferences,
    ) {
        val storage = checkNotNull(map[key])
        storage.update(transform)
    }
}
