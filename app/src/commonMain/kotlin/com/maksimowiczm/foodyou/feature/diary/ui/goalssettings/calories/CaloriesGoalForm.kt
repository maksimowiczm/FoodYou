package com.maksimowiczm.foodyou.feature.diary.ui.goalssettings.calories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
private fun FormFieldWithTextFieldValue<Int, GoalsFormInputError>.TextField(
    suffix: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )
) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        label = label,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(error.stringResource())
            }
        },
        suffix = suffix,
        keyboardOptions = keyboardOptions,
        maxLines = 1,
        interactionSource = interactionSource
    )
}

@Composable
private fun FormFieldWithTextFieldValue<Float, GoalsFormInputError>.FloatTextField(
    suffix: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )
) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        label = label,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(error.stringResource())
            }
        },
        suffix = suffix,
        keyboardOptions = keyboardOptions,
        maxLines = 1,
        interactionSource = interactionSource
    )
}

@Composable
fun CaloriesGoalForm(state: CaloriesGoalFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.calories.TextField(
            label = { Text(stringResource(Res.string.unit_calories)) },
            suffix = { Text(stringResource(Res.string.unit_kcal)) }
        )

        Column {
            Text(
                text = stringResource(Res.string.nutriment_proteins),
                color = nutrientsPalette.proteinsOnSurfaceContainer,
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.proteinsGrams.TextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
                state.proteinsPercentage.FloatTextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text("%") }
                )
            }
        }

        Column {
            Text(
                text = stringResource(Res.string.nutriment_carbohydrates),
                color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.carbohydratesGrams.TextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
                state.carbohydratesPercentage.FloatTextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text("%") }
                )
            }
        }

        Column {
            Text(
                text = stringResource(Res.string.nutriment_fats),
                color = nutrientsPalette.fatsOnSurfaceContainer,
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.fatsGrams.TextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
                state.fatsPercentage.FloatTextField(
                    modifier = Modifier.weight(1f),
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        state.error?.let {
            Text(
                text = it.stringResource(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
