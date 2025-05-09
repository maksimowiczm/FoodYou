package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.meal.data.observeMealCardsLayout
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class MealsCardViewModel(
    private val observeMealsWithSummaryUseCase: ObserveMealsWithSummaryUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val layout = dataStore.observeMealCardsLayout().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking { dataStore.observeMealCardsLayout().first() }
    )

    fun observeMeals(date: LocalDate) = observeMealsWithSummaryUseCase(
        date = date
    )
}
