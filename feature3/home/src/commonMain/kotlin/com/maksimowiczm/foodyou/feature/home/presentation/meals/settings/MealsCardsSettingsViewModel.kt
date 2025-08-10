package com.maksimowiczm.foodyou.feature.home.presentation.meals.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealsPreferencesCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsPreferencesQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class MealsCardsSettingsViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {

    private val _preferences = queryBus.dispatch<MealsPreferences>(ObserveMealsPreferencesQuery)
    val preferences =
        _preferences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _preferences.first() },
        )

    fun updatePreferences(preferences: MealsPreferences) {
        viewModelScope.launch { commandBus.dispatch(UpdateMealsPreferencesCommand(preferences)) }
    }
}
