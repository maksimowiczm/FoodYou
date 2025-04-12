package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.measurement.MeasurableFood
import com.maksimowiczm.foodyou.feature.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient

@Composable
internal fun UpdateIngredientMeasurementScreen(
    food: MeasurableFood,
    ingredient: Ingredient,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: () -> Unit,
    onDeleteFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    var replaced = false
    val suggestions = food.suggestions
    val realSuggestions = suggestions.map {
        if (replaced) {
            return@map it
        }

        val real = when (it) {
            is Measurement.Gram ->
                ingredient.measurement as? Measurement.Gram
                    ?: return@map it

            is Measurement.Package ->
                ingredient.measurement as? Measurement.Package
                    ?: return@map it

            is Measurement.Serving ->
                ingredient.measurement as? Measurement.Serving
                    ?: return@map it
        }

        replaced = true
        real
    }

    val food = food.copy(
        suggestions = realSuggestions,
        selected = ingredient.measurement
    )

    MeasurementScreen(
        food = food,
        selectedMeasurement = ingredient.measurement,
        onBack = onBack,
        onMeasurement = onMeasurement,
        onEditFood = onEditFood,
        onDeleteFood = onDeleteFood,
        modifier = modifier
    )
}
