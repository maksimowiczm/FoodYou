package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.importexport.tbca.domain.ImportTBCAUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class OnboardingViewModel(
    private val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
    private val importTBCAUseCase: ImportTBCAUseCase,
) : ViewModel() {
    private val eventBus = MutableStateFlow<OnboardingEvent?>(null)
    val events = eventBus.filterNotNull()

    private val mutex = Mutex()

    /**
     * Finishes onboarding by updating preferences and auto-importing TBCA database.
     */
    fun finish() {
        if (mutex.isLocked) {
            return
        }

        viewModelScope.launch {
            mutex.withLock {
                // Update OpenFoodFacts preference (always enabled for barcode scanner)
                foodSearchPreferencesRepository.update {
                    copy(
                        openFoodFacts = openFoodFacts.copy(enabled = true), // Always enabled
                    )
                }

                // Auto-import TBCA database on first launch
                try {
                    importTBCAUseCase.import().collect { count ->
                        // Collecting to ensure import completes
                        // Progress tracking could be added here if needed
                    }
                } catch (e: Exception) {
                    // Log error but don't block onboarding
                    println("Warning: TBCA import failed during onboarding: ${e.message}")
                }

                eventBus.emit(OnboardingEvent.Finished)
            }
        }
    }
}
