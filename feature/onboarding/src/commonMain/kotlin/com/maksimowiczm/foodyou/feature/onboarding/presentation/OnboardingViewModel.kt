package com.maksimowiczm.foodyou.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.feature.onboarding.ui.OnboardingState
import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchPreferences
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class OnboardingViewModel(
    private val importSwissUseCase: ImportSwissFoodCompositionDatabaseUseCase,
    private val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
) : ViewModel() {
    private val eventBus = MutableStateFlow<OnboardingEvent?>(null)
    val events = eventBus.filterNotNull()

    private val mutex = Mutex()

    fun finish(state: OnboardingState) {
        if (mutex.isLocked) {
            FoodYouLogger.e(TAG) { "Finish called while mutex is locked" }
            return
        }

        val useOpenFoodFacts = state.useOpenFoodFacts
        val useUsda = state.useUsda
        val languages = state.swissLanguages

        viewModelScope.launch {
            mutex.withLock {
                foodSearchPreferencesRepository.update {
                    copy(
                        usda = usda.copy(enabled = useUsda),
                        openFoodFacts = openFoodFacts.copy(enabled = useOpenFoodFacts),
                    )
                }

                if (languages.isNotEmpty()) {
                    importSwissUseCase.import(languages).last()
                }

                eventBus.emit(OnboardingEvent.Finished)
            }
        }
    }

    private companion object {
        const val TAG = "OnboardingViewModel"
    }
}
