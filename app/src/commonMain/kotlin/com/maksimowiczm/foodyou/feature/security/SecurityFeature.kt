package com.maksimowiczm.foodyou.feature.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.SettingsFeature
import com.maksimowiczm.foodyou.feature.security.data.SecurityPreferences
import com.maksimowiczm.foodyou.feature.security.ui.SecureScreenSettingsListItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object SecurityFeature : Feature.Settings() {
    override fun build(navController: NavController) = SettingsFeature { modifier ->
        SecureScreenSettingsListItem(modifier)
    }

    override suspend fun KoinComponent.initialize() {
        val dataStore: DataStore<Preferences> = get()

        dataStore.edit {
            if (it[SecurityPreferences.hideContent] == null) {
                it[SecurityPreferences.hideContent] = false
            }
        }
    }
}
