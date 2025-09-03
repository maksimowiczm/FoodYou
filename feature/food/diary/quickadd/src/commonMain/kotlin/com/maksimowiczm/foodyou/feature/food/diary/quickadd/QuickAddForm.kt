package com.maksimowiczm.foodyou.feature.food.diary.quickadd

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.unorderedList
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuickAddForm(state: QuickAddFormState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        OutlinedTextField(
            state = state.name.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.product_name)) },
            supportingText = { Text(stringResource(Res.string.neutral_required)) },
            isError = state.name.error != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        state.proteins.TextField(
            label = stringResource(Res.string.nutriment_proteins),
            modifier = Modifier.fillMaxWidth(),
        )
        state.carbohydrates.TextField(
            label = stringResource(Res.string.nutriment_carbohydrates),
            modifier = Modifier.fillMaxWidth(),
        )
        state.fats.TextField(
            label = stringResource(Res.string.nutriment_fats),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            state = state.energy.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.unit_energy)) },
            supportingText = {
                val error = state.energy.error
                if (error != null) {
                    Text(error.stringResource())
                }
            },
            suffix = { Text(stringResource(Res.string.unit_kcal)) },
            trailingIcon = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text =
                                    if (state.autoCalculateEnergy) {
                                        stringResource(Res.string.headline_auto_calculate_energy)
                                    } else {
                                        stringResource(Res.string.headline_manual_energy_input)
                                    },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    state = rememberTooltipState(isPersistent = true),
                ) {
                    IconButton(
                        onClick = { state.autoCalculateEnergy = !state.autoCalculateEnergy }
                    ) {
                        if (state.autoCalculateEnergy) {
                            Icon(imageVector = Icons.Outlined.Calculate, contentDescription = null)
                        } else {
                            Icon(imageVector = Icons.Outlined.Keyboard, contentDescription = null)
                        }
                    }
                }
            },
            isError = state.energy.error != null,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        )

        Text(
            text = stringResource(Res.string.description_calories_are_calculated),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text =
                unorderedList(
                    stringResource(
                        Res.string.x_kcal_per_g,
                        stringResource(Res.string.nutriment_proteins),
                        4,
                    ),
                    stringResource(
                        Res.string.x_kcal_per_g,
                        stringResource(Res.string.nutriment_carbohydrates),
                        4,
                    ),
                    stringResource(
                        Res.string.x_kcal_per_g,
                        stringResource(Res.string.nutriment_fats),
                        9,
                    ),
                ),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun FormField<Double?, QuickAddFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
        supportingText = {
            val error = error
            if (error != null) {
                Text(error.stringResource())
            }
        },
        isError = error != null,
        label = { Text(label) },
    )
}
