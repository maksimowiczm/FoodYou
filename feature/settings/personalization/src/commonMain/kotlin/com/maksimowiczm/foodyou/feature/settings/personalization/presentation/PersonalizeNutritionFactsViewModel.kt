package com.maksimowiczm.foodyou.feature.settings.personalization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class PersonalizeNutritionFactsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _order = settingsRepository.observe().map { it.nutrientsOrder }

    val order =
        _order.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _order.first() },
        )

    fun resetOrder() {
        viewModelScope.launch {
            settingsRepository.update { copy(nutrientsOrder = NutrientsOrder.defaultOrder) }
        }
    }

    fun updateOrder(order: List<NutrientsOrder>) {
        viewModelScope.launch { settingsRepository.update { copy(nutrientsOrder = order) } }
    }
}
