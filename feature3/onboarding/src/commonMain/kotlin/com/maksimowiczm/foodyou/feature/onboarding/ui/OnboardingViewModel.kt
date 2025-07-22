package com.maksimowiczm.foodyou.feature.onboarding.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.preferences.UseUSDA
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain.ImportSwissDatabaseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class OnboardingViewModel(
    dataStore: DataStore<Preferences>,
    private val importSwissDatabaseUseCase: ImportSwissDatabaseUseCase
) : ViewModel() {
    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()
    private val useUsda = dataStore.userPreference<UseUSDA>()

    private val eventBus = MutableStateFlow<OnboardingEvent?>(null)
    val events = eventBus.filterNotNull()

    fun finish(state: OnboardingState) {
        viewModelScope.launch {
            useOpenFoodFacts.set(state.useOpenFoodFacts)
            useUsda.set(state.useUsda)

            Logger.d(TAG) { "Importing Swiss database..." }
            importSwissDatabaseUseCase.import(state.swissLanguages)
            Logger.d(TAG) { "Swiss database imported successfully." }

            eventBus.emit(OnboardingEvent.Finish)
        }
    }

    private companion object {
        const val TAG = "OnboardingViewModel"
    }
}
