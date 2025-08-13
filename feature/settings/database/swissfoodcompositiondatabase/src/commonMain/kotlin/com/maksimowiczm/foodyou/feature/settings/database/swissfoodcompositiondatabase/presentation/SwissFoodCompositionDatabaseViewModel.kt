package com.maksimowiczm.foodyou.feature.settings.database.swissfoodcompositiondatabase.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.ImportCsvProductsCommand
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.externaldatabase.swissfoodcompositiondatabase.Language
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SwissFoodCompositionDatabaseViewModel(private val commandBus: CommandBus) :
    ViewModel() {

    private val _uiState =
        MutableStateFlow<SwissFoodCompositionDatabaseUiState>(
            SwissFoodCompositionDatabaseUiState.LanguagePick
        )
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun import(languages: Set<Language>) {
        if (mutex.isLocked) {
            return
        }

        viewModelScope.launch {
            mutex.withLock {
                _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(0f)

                val size = languages.sumOf { it.size }
                var count = 0f

                languages.asFlow().flatMapConcat(::importLanguage).collectLatest {
                    count++

                    _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(count / size)
                }

                delay(200)
                _uiState.value = SwissFoodCompositionDatabaseUiState.Finished
            }
        }
    }

    private suspend fun importLanguage(language: Language): Flow<Int> {
        val lines =
            language.readBytes().decodeToString().split("\n").drop(1).filterNot { it.isBlank() }

        val result =
            commandBus.dispatch(
                ImportCsvProductsCommand(
                    mapper = order,
                    lines = lines.asFlow(),
                    source = FoodSource.Type.SwissFoodCompositionDatabase,
                )
            ) as Result.Success

        return result.data
    }
}

private val order =
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
