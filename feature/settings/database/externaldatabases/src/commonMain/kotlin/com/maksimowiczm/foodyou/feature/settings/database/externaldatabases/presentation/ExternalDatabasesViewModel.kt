package com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseOpenFoodFactsCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseUsda
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQuery
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ExternalDatabasesViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {

    val foodPreferences =
        queryBus
            .dispatch(ObserveFoodPreferencesQuery)
            .map(::FoodPreferencesModel)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodPreferencesModel(),
            )

    fun toggleOpenFoodFacts(newState: Boolean) {
        viewModelScope.launch { commandBus.dispatch(UpdateUseOpenFoodFactsCommand(newState)) }
    }

    fun toggleUsda(newState: Boolean) {
        viewModelScope.launch { commandBus.dispatch(UpdateUseUsda(newState)) }
    }
}
