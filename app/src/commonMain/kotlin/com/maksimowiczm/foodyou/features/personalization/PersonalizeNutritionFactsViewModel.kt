package com.maksimowiczm.foodyou.features.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import kotlin.time.Clock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizeNutritionFactsViewModel(private val accountService: AccountService) : ViewModel() {
    private val eventBus = Channel<PersonalizeNutritionFactsEvent>()
    val events = eventBus.receiveAsFlow()

    private val _order = accountService.observe().filterNotNull().map { it.nutrientsOrder }

    val order =
        _order.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _order.first() },
        )

    fun updateOrder(order: List<NutrientsOrder>) {
        viewModelScope.launch {
            accountService.handle(AccountCommand.ChangeNutrientsOrder(order, Clock.System.now()))
            eventBus.send(PersonalizeNutritionFactsEvent.Updated)
        }
    }
}
