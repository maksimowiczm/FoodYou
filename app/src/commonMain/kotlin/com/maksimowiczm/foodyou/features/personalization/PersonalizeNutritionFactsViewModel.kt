package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.preferences.domain.NutrientsOrderPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import com.maksimowiczm.foodyou.preferences.domain.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizeNutritionFactsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val eventBus = Channel<PersonalizeNutritionFactsEvent>()
    val events = eventBus.receiveAsFlow()

    private val _order =
        preferencesRepository.observe<NutrientsOrderPreference>().map { it.nutrientsOrder }

    val order =
        _order.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _order.first() },
        )

    fun updateOrder(order: List<NutrientsOrder>) {
        viewModelScope.launch {
            preferencesRepository.update<NutrientsOrderPreference> {
                NutrientsOrderPreference(order)
            }
            eventBus.send(PersonalizeNutritionFactsEvent.Updated)
        }
    }
}
