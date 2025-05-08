package com.maksimowiczm.foodyou.ui.settings.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeSettingsViewModel : ViewModel() {
    val order: StateFlow<List<HomeCard>> = MutableStateFlow(
        listOf(
            HomeCard.Calendar,
            HomeCard.Meals,
            HomeCard.Calories
        )
    )

    fun reorder(newOrder: List<HomeCard>) {
        (order as MutableStateFlow).value = newOrder
    }
}
