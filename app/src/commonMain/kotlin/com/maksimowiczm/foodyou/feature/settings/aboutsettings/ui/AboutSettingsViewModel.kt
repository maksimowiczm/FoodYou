package com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.preferences.AboutPreferences
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AboutSettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    val githubStar = dataStore
        .observe(AboutPreferences.githubStarClicked)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = runBlocking {
                dataStore.get(AboutPreferences.githubStarClicked) ?: false
            }
        )

    // If user clicks we happy
    fun onGithubStarClick() {
        viewModelScope.launch {
            dataStore.set(
                AboutPreferences.githubStarClicked to true
            )
        }
    }
}
