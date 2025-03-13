package com.maksimowiczm.foodyou.feature.settings.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.data.SecurityPreferences
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.security.ui.SecureScreenSettingsListItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object SecureScreenSettings : Feature.Settings {
    override fun buildSettingsFeatures(navController: NavController) = SettingsFeature { modifier ->
        SecureScreenSettingsListItem(modifier)
    }

    override suspend fun KoinComponent.initialize() {
        val dataStore: DataStore<Preferences> = get()

        dataStore.edit {
            if (it[SecurityPreferences.showContent] != null) {
                return@edit
            }

            it[SecurityPreferences.showContent] = true
        }
    }
}
