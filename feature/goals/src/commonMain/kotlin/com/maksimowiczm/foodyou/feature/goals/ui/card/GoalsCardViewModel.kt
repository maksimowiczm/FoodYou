package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.feature.goals.data.GoalsPreferences
import com.maksimowiczm.foodyou.feature.goals.domain.GoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class GoalsCardViewModel(
    private val repository: GoalsRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = repository.observeDiaryDay(date)

    val expand: StateFlow<Boolean> = dataStore
        .observe(GoalsPreferences.expandGoalsCard)
        .map { it ?: true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking {
                dataStore.getBlocking(GoalsPreferences.expandGoalsCard) ?: true
            }
        )
}
