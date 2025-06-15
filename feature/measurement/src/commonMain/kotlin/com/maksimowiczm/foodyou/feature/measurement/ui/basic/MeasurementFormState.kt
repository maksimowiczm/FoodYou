package com.maksimowiczm.foodyou.feature.measurement.ui.basic

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult.Failure
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult.Success
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField

private val parser: (String) -> ParseResult<Float, String> by lazy {
    {
        it.toFloatOrNull()?.let { value ->
            when {
                value <= 0f -> Failure("Negative number")
                else -> Success(value)
            }
        } ?: Failure("Invalid number")
    }
}

@Composable
fun rememberMeasurementFormState(
    food: Food,
    suggestions: Map<MeasurementEnum, Float>,
    selected: MeasurementEnum?
): MeasurementFormState {
    val packageInput = food.totalWeight?.let {
        val suggestion = suggestions[MeasurementEnum.Package] ?: 1f

        rememberFormField(
            initialValue = suggestion,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = suggestion.formatClipZeros())
        )
    }

    val servingInput = food.servingWeight?.let {
        val suggestion = suggestions[MeasurementEnum.Serving] ?: 1f

        rememberFormField(
            initialValue = suggestion,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = suggestion.formatClipZeros())
        )
    }

    val gramInput = if (!food.isLiquid) {
        rememberFormField(
            initialValue = suggestions[MeasurementEnum.Gram] ?: 100f,
            parser = parser,
            textFieldState = rememberTextFieldState(
                initialText = suggestions[MeasurementEnum.Gram]?.formatClipZeros() ?: "100"
            )
        )
    } else {
        null
    }

    val milliliterInput = if (food.isLiquid) {
        rememberFormField(
            initialValue = suggestions[MeasurementEnum.Milliliter] ?: 100f,
            parser = parser,
            textFieldState = rememberTextFieldState(
                initialText = suggestions[MeasurementEnum.Milliliter]?.formatClipZeros() ?: "100"
            )
        )
    } else {
        null
    }

    return remember(packageInput, servingInput, gramInput, milliliterInput, food, selected) {
        MeasurementFormState(
            packageInput,
            food.totalWeight,
            servingInput,
            food.servingWeight,
            gramInput,
            milliliterInput,
            food.nutritionFacts,
            selected
        )
    }
}

@Composable
fun rememberMeasurementFormState(
    food: Food,
    selected: MeasurementEnum? = null
): MeasurementFormState {
    val packageInput = food.totalWeight?.let {
        rememberFormField(
            initialValue = 1f,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = "1")
        )
    }

    val servingInput = food.servingWeight?.let {
        rememberFormField(
            initialValue = 1f,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = "1")
        )
    }

    val gramInput = if (!food.isLiquid) {
        rememberFormField(
            initialValue = 100f,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = "100")
        )
    } else {
        null
    }

    val milliliterInput = if (food.isLiquid) {
        rememberFormField(
            initialValue = 100f,
            parser = parser,
            textFieldState = rememberTextFieldState(initialText = "100")
        )
    } else {
        null
    }

    return remember(packageInput, servingInput, gramInput, milliliterInput, food, selected) {
        MeasurementFormState(
            packageInput,
            food.totalWeight,
            servingInput,
            food.servingWeight,
            gramInput,
            milliliterInput,
            food.nutritionFacts,
            selected
        )
    }
}

@Stable
class MeasurementFormState(
    val packageInput: FormField<Float, String>?,
    val packageWeight: Float?,
    val servingInput: FormField<Float, String>?,
    val servingWeight: Float?,
    val gramInput: FormField<Float, String>?,
    val milliliterInput: FormField<Float, String>?,
    val nutrients: NutritionFacts,
    val selected: MeasurementEnum?
) {
    // Hacky way to determine if the food is liquid based on the presence of milliliter input.
    val isLiquid: Boolean
        get() = milliliterInput != null
}
