package com.maksimowiczm.foodyou.feature.measurement.ui.basic

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Nutrients
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
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
    val packageInput = food.packageWeight?.let {
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

    val gramInput = rememberFormField(
        initialValue = suggestions[MeasurementEnum.Gram] ?: 100f,
        parser = parser,
        textFieldState = rememberTextFieldState(
            initialText = suggestions[MeasurementEnum.Gram]?.formatClipZeros() ?: "100"
        )
    )

    return remember(packageInput, servingInput, gramInput, food, selected) {
        MeasurementFormState(
            packageInput,
            food.packageWeight,
            servingInput,
            food.servingWeight,
            gramInput,
            food.nutrients,
            selected
        )
    }
}

@Composable
fun rememberMeasurementFormState(
    food: Food,
    selected: MeasurementEnum? = null
): MeasurementFormState {
    val packageInput = food.packageWeight?.let {
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

    val gramInput = rememberFormField(
        initialValue = 100f,
        parser = parser,
        textFieldState = rememberTextFieldState(initialText = "100")
    )

    return remember(packageInput, servingInput, gramInput, food, selected) {
        MeasurementFormState(
            packageInput,
            food.packageWeight,
            servingInput,
            food.servingWeight,
            gramInput,
            food.nutrients,
            selected
        )
    }
}

@Stable
class MeasurementFormState(
    val packageInput: FormField<Float, String>?,
    val packageWeight: PortionWeight.Package?,
    val servingInput: FormField<Float, String>?,
    val servingWeight: PortionWeight.Serving?,
    val gramInput: FormField<Float, String>,
    val nutrients: Nutrients,
    val selected: MeasurementEnum?
)
