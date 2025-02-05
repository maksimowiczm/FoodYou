package com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryPreferences
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import com.maksimowiczm.foodyou.core.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.core.infrastructure.datastore.set
import kotlinx.coroutines.flow.SharingStarted
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
    val expanded = dataStore.observe(DiaryPreferences.caloriesExpanded).map {
        it ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = runBlocking { dataStore.get(DiaryPreferences.caloriesExpanded) ?: false }
    )

    fun onExpandedChange(expanded: Boolean) {
        viewModelScope.launch {
            dataStore.set(
                DiaryPreferences.caloriesExpanded to expanded
            )
        }
    }
}
