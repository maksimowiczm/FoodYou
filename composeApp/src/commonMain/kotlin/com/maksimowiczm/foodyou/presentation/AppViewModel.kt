package com.maksimowiczm.foodyou.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.business.settings.domain.EnergyFormat
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.ui.utils.EnergyFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class AppViewModel(queryBus: QueryBus, private val commandBus: CommandBus) : ViewModel() {

    private val settings = queryBus.dispatch(ObserveSettingsQuery)

    val nutrientsOrder =
        settings
            .map { it.nutrientsOrder }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = NutrientsOrder.defaultOrder,
            )

    val onboardingFinished =
        settings
            .map { it.onboardingFinished }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { settings.map { it.onboardingFinished }.first() },
            )

    val energyFormatter =
        settings
            .map { it.energyFormat.toFormatter() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyFormat.DEFAULT.toFormatter(),
            )

    fun finishOnboarding() {
        viewModelScope.launch {
            commandBus.dispatch(PartialSettingsUpdateCommand(onboardingFinished = true))
        }
    }
}

private fun EnergyFormat.toFormatter(): EnergyFormatter =
    when (this) {
        EnergyFormat.Kilocalories -> EnergyFormatter.kilocalories
        EnergyFormat.Kilojoules -> EnergyFormatter.kilojoules
    }
