package com.maksimowiczm.foodyou.feature.meal.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.meal.data.MealPreferences
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class MealsCardViewModel(
    private val observeMealsWithSummaryUseCase: ObserveMealsWithSummaryUseCase,
    private val dateFormatter: DateFormatter,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    fun observeMeals(date: LocalDate) = observeMealsWithSummaryUseCase(
        date = date
    )

    fun formatTime(time: LocalTime) = dateFormatter.formatTime(time)

    val useVerticalLayout = dataStore
        .observe(MealPreferences.useVerticalLayout)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { dataStore.get(MealPreferences.useVerticalLayout) ?: false }
        )

    val useCompactLayout = dataStore
        .observe(MealPreferences.useCompactLayout)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { dataStore.get(MealPreferences.useCompactLayout) ?: false }
        )
}
