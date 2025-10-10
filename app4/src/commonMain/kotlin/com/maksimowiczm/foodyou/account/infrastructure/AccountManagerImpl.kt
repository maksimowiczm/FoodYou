package com.maksimowiczm.foodyou.account.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountManagerImpl(private val dataStore: DataStore<Preferences>) : AccountManager {
    override suspend fun setPrimaryAccountId(accountId: LocalAccountId) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[AccountManagerKeys.primaryAccountId] = accountId.value
            }
        }
    }

    override fun observePrimaryAccountId(): Flow<LocalAccountId?> {
        return dataStore.data.map { prefs ->
            val id = prefs[AccountManagerKeys.primaryAccountId]
            id?.let(::LocalAccountId)
        }
    }
}

private object AccountManagerKeys {
    val primaryAccountId = stringPreferencesKey("account:account_manager:primary_account_id")
}
