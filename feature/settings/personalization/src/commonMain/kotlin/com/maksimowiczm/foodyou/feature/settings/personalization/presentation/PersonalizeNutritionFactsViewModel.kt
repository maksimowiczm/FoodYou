package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class PersonalizeNutritionFactsViewModel(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val commandBus: CommandBus,
) : ViewModel() {

    private val _order = observeSettingsUseCase.observe().map { it.nutrientsOrder }

    val order =
        _order.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _order.first() },
        )

    fun resetOrder() {
        viewModelScope.launch {
            commandBus.dispatch(
                PartialSettingsUpdateCommand(nutrientsOrder = NutrientsOrder.defaultOrder)
            )
        }
    }

    fun updateOrder(order: List<NutrientsOrder>) {
        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(nutrientsOrder = order))
        }
    }
}
