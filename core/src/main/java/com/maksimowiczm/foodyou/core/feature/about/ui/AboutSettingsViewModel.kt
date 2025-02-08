package com.maksimowiczm.foodyou.core.feature.about.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.feature.about.data.AboutPreferences
import com.maksimowiczm.foodyou.core.feature.about.data.LinkHandler
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import com.maksimowiczm.foodyou.core.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.core.infrastructure.datastore.set
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AboutSettingsViewModel(
    private val linkHandler: LinkHandler,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
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
            linkHandler.openGithubRepository()
        }
    }

    fun openGithubIssue() {
        linkHandler.openGithubIssue()
    }

    fun openGithubReadme() {
        linkHandler.openGithubReadme()
    }
}
