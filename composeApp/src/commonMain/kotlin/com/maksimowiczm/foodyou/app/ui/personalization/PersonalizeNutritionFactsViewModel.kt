package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonalizeNutritionFactsViewModel(
    private val appAccountManager: AppAccountManager,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _order = appAccountManager.observeAppAccount().map { it.settings.nutrientsOrder }

    val order =
        _order.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _order.first() },
        )

    fun updateOrder(order: List<NutrientsOrder>) {
        viewModelScope.launch {
            val account = appAccountManager.observeAppAccount().first()
            account.updateSettings { it.copy(nutrientsOrder = order) }
            accountRepository.save(account)
        }
    }
}
