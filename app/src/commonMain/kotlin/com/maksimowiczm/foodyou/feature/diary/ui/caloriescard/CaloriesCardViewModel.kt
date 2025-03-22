package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.DiaryPreferences
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CaloriesCardViewModel(
    diaryRepository: DiaryRepository,
    private val dataStore: DataStore<Preferences>
) : DiaryViewModel(
    diaryRepository
) {
    val state = dataStore.observeCaloriesCardState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = runBlocking { dataStore.observeCaloriesCardState().first() }
    )

    fun toggleCaloriesCardState() {
        viewModelScope.launch {
            dataStore.edit {
                val currentState = state.value
                val newState = when (currentState) {
                    CaloriesCardState.Compact -> 1
                    CaloriesCardState.Default -> 2
                    CaloriesCardState.Expanded -> 0
                }

                it[DiaryPreferences.caloriesCardState] = newState
            }
        }
    }

    private fun DataStore<Preferences>.observeCaloriesCardState(): Flow<CaloriesCardState> =
        dataStore.observe(DiaryPreferences.caloriesCardState).map {
            when (it) {
                0 -> CaloriesCardState.Compact
                1 -> CaloriesCardState.Default
                2 -> CaloriesCardState.Expanded
                else -> CaloriesCardState.Default
            }
        }
}

enum class CaloriesCardState {
    Compact,
    Default,
    Expanded
}
