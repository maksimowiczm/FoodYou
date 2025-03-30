package com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases.ObserveProductCase
import org.koin.compose.koinInject

@Composable
fun MeasurementFormScreen(
    foodId: FoodId,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: (FoodId) -> Unit,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurement) -> Unit,
    modifier: Modifier = Modifier,
    observeProductCase: ObserveProductCase = koinInject()
) {
    val foodState = observeProductCase(foodId).collectAsStateWithLifecycle(null)

    when (val food = foodState.value) {
        null -> Surface(Modifier.fillMaxSize()) {}
        else -> MeasurementForm(
            name = food.name,
            nutrients = food.nutrients,
            packageWeight = food.packageWeight,
            servingWeight = food.servingWeight,
            state = rememberMeasurementFormState(
                suggestion = food.suggestion,
                highlight = food.highlight
            ),
            onEdit = { onEditFood(food.id) },
            onDelete = { onDeleteFood(food.id) },
            onConfirm = onConfirm,
            onBack = onBack,
            modifier = modifier
        )
    }
}

@Composable
fun MeasurementFormScreen(
    foodId: FoodId,
    highlight: WeightMeasurement,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: (FoodId) -> Unit,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurement) -> Unit,
    modifier: Modifier = Modifier,
    observeProductCase: ObserveProductCase = koinInject()
) {
    val foodState = observeProductCase(foodId).collectAsStateWithLifecycle(null)

    when (val food = foodState.value) {
        null -> Surface(Modifier.fillMaxSize()) {}
        else -> {
            val suggestion = food.suggestion.copy(
                packageSuggestion = highlight as? WeightMeasurement.Package
                    ?: food.suggestion.packageSuggestion,
                servingSuggestion = highlight as? WeightMeasurement.Serving
                    ?: food.suggestion.servingSuggestion,
                weightSuggestion = highlight as? WeightMeasurement.WeightUnit
                    ?: food.suggestion.weightSuggestion
            )

            MeasurementForm(
                name = food.name,
                nutrients = food.nutrients,
                packageWeight = food.packageWeight,
                servingWeight = food.servingWeight,
                state = rememberMeasurementFormState(
                    suggestion = suggestion,
                    highlight = highlight.asEnum()
                ),
                onEdit = { onEditFood(food.id) },
                onDelete = { onDeleteFood(food.id) },
                onConfirm = onConfirm,
                onBack = onBack,
                modifier = modifier
            )
        }
    }
}

@Composable
fun MeasurementFormScreen(
    measurementId: MeasurementId,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: (FoodId) -> Unit,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurement) -> Unit,
    modifier: Modifier = Modifier,
    observeProductCase: ObserveProductCase = koinInject()
) {
    val foodState = observeProductCase(measurementId).collectAsStateWithLifecycle(null)

    when (val food = foodState.value) {
        null -> Surface(Modifier.fillMaxSize()) {}
        else -> MeasurementForm(
            name = food.name,
            nutrients = food.nutrients,
            packageWeight = food.packageWeight,
            servingWeight = food.servingWeight,
            state = rememberMeasurementFormState(
                suggestion = food.suggestion,
                highlight = food.highlight
            ),
            onEdit = { onEditFood(food.id) },
            onDelete = { onDeleteFood(food.id) },
            onConfirm = onConfirm,
            onBack = onBack,
            modifier = modifier
        )
    }
}
