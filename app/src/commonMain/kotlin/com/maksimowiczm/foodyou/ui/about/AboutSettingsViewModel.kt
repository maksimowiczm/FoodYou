package com.maksimowiczm.foodyou.ui.about

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.data.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AboutSettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    val githubStar = dataStore
        .observe(AppPreferences.githubStarClicked)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = runBlocking {
                dataStore.get(AppPreferences.githubStarClicked) ?: false
            }
        )

    // If user clicks we happy
    fun onGithubStarClick() {
        viewModelScope.launch {
            dataStore.set(AppPreferences.githubStarClicked to true)
        }
    }
}
