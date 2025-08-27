package com.maksimowiczm.foodyou.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.externaldatabase.swissfoodcompositiondatabase.Language
import com.maksimowiczm.foodyou.feature.onboarding.ui.OnboardingState
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class OnboardingViewModel(
    private val importCsvProductUseCase: ImportCsvProductUseCase,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
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

                languages.forEach { importLanguage(it) }

                eventBus.emit(OnboardingEvent.Finished)
            }
        }
    }

    private suspend fun importLanguage(language: Language) {
        val lines =
            language.readBytes().decodeToString().split("\n").drop(1).filterNot { it.isBlank() }

        importCsvProductUseCase
            .import(
                mapper = swissOrder,
                lines = lines.asFlow(),
                source = FoodSource.Type.SwissFoodCompositionDatabase,
            )
            .last()

        FoodYouLogger.d(TAG) { "Imported products for language $language" }
    }

    private companion object {
        const val TAG = "OnboardingViewModel"
    }
}

private val swissOrder =
    listOf(
        ProductField.Name,
        ProductField.Brand,
        ProductField.Barcode,
        ProductField.Proteins,
        ProductField.Carbohydrates,
        ProductField.Fats,
        ProductField.Energy,
        ProductField.SaturatedFats,
        ProductField.MonounsaturatedFats,
        ProductField.PolyunsaturatedFats,
        ProductField.Omega3,
        ProductField.Omega6,
        ProductField.Sugars,
        ProductField.Salt,
        ProductField.DietaryFiber,
        ProductField.Cholesterol,
        ProductField.Caffeine,
        ProductField.VitaminA,
        ProductField.VitaminB1,
        ProductField.VitaminB2,
        ProductField.VitaminB3,
        ProductField.VitaminB5,
        ProductField.VitaminB6,
        ProductField.VitaminB7,
        ProductField.VitaminB9,
        ProductField.VitaminB12,
        ProductField.VitaminC,
        ProductField.VitaminD,
        ProductField.VitaminE,
        ProductField.VitaminK,
        ProductField.Manganese,
        ProductField.Magnesium,
        ProductField.Potassium,
        ProductField.Calcium,
        ProductField.Copper,
        ProductField.Zinc,
        ProductField.Sodium,
        ProductField.Iron,
        ProductField.Phosphorus,
        ProductField.Selenium,
        ProductField.Iodine,
        ProductField.PackageWeight,
        ProductField.ServingWeight,
    )
