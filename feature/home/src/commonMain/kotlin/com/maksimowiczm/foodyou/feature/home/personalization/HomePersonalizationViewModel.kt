package com.maksimowiczm.foodyou.feature.home.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.HomeCard
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.Settings
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class HomePersonalizationViewModel(
    private val settingsRepository: UserPreferencesRepository<Settings>
) : ViewModel() {

    private val _homeOrder = settingsRepository.observe().map { it.homeCardOrder }
    val homeOrder =
        _homeOrder.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _homeOrder.first() },
        )

    fun updateOrder(order: List<HomeCard>) {
        viewModelScope.launch { settingsRepository.update { copy(homeCardOrder = order) } }
    }
}
