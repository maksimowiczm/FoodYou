package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.core.ui.form.floatParser
import com.maksimowiczm.foodyou.core.ui.form.intParser
import com.maksimowiczm.foodyou.core.ui.form.rememberFormField
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.unit_energy
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transformLatest
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun MacroWeightInputForm(state: MacroWeightInputFormState, modifier: Modifier = Modifier) {
    Column(modifier) {
        AnimatedVisibility(state.autoCalculateEnergy) {
            Column {
                Spacer(Modifier.height(8.dp))
                AutoCalculateCard(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        var badEnergy by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(state) {
            snapshotFlow { state.badEnergy }.transformLatest {
                if (it) {
                    delay(1.seconds)
                }

                emit(it)
            }.collectLatest {
                badEnergy = it
            }
        }
        AnimatedVisibility(badEnergy) {
            Column {
                Spacer(Modifier.height(8.dp))
                BadEnergyCard(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        MacroInput(
            state = state
        )
    }
}

@Composable
internal fun rememberMacroWeightInputFormState(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    energy: Int
): MacroWeightInputFormState {
    val proteinsField = rememberFormField(
        initialValue = proteins,
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.Required }
        ),
        textFieldState = rememberTextFieldState(proteins.formatClipZeros())
    )

    val carbohydratesField = rememberFormField(
        initialValue = carbohydrates,
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.Required }
        ),
        textFieldState = rememberTextFieldState(carbohydrates.formatClipZeros())
    )

    val fatsField = rememberFormField(
        initialValue = fats,
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.Required }
        ),
        textFieldState = rememberTextFieldState(fats.formatClipZeros())
    )

    val energyField = rememberFormField(
        initialValue = energy,
        parser = intParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.Required }
        ),
        textFieldState = rememberTextFieldState(energy.toString())
    )

    val autoCalculateEnergy = rememberSaveable {
        val currentEnergy = NutrientsHelper.calculateEnergy(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats
        ).roundToInt()

        // Allow 2% error
        val allowedError = (currentEnergy * 0.02).toInt()
        val shouldAutoCalculate =
            energy in (currentEnergy - allowedError)..(currentEnergy + allowedError)

        mutableStateOf(shouldAutoCalculate)
    }

    LaunchedEffect(
        autoCalculateEnergy.value,
        proteinsField.value,
        carbohydratesField.value,
        fatsField.value
    ) {
        if (autoCalculateEnergy.value) {
            val kcal = NutrientsHelper.calculateEnergy(
                proteins = proteinsField.value,
                carbohydrates = carbohydratesField.value,
                fats = fatsField.value
            ).roundToInt()

            energyField.textFieldState.setTextAndPlaceCursorAtEnd(kcal.toString())
        }
    }

    return MacroWeightInputFormState(
        proteins = proteinsField,
        carbohydrates = carbohydratesField,
        fats = fatsField,
        energy = energyField,
        autoCalculateEnergyState = autoCalculateEnergy
    )
}

@Stable
internal class MacroWeightInputFormState(
    val proteins: FormField<Float, DailyGoalsFormFieldError>,
    val carbohydrates: FormField<Float, DailyGoalsFormFieldError>,
    val fats: FormField<Float, DailyGoalsFormFieldError>,
    val energy: FormField<Int, DailyGoalsFormFieldError>,
    autoCalculateEnergyState: MutableState<Boolean>
) {
    var autoCalculateEnergy by autoCalculateEnergyState

    val badEnergy by derivedStateOf {
        val kcal = NutrientsHelper.calculateEnergy(
            proteins = proteins.value,
            carbohydrates = carbohydrates.value,
            fats = fats.value
        ).roundToInt()

        // Allow 2% error
        val allowedError = (kcal * 0.02).toInt()
        val energyValue = energy.value
        energyValue < (kcal - allowedError) || energyValue > (kcal + allowedError)
    }

    fun autoSetEnergy() {
        val kcal = NutrientsHelper.calculateEnergy(
            proteins = proteins.value,
            carbohydrates = carbohydrates.value,
            fats = fats.value
        ).roundToInt()

        energy.textFieldState.setTextAndPlaceCursorAtEnd(kcal.toString())
    }
}

@Composable
private fun BadEnergyCard(state: MacroWeightInputFormState, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null
            )
            Text(
                text = "The total calories from your proteins, carbohydrates, and fat don't add up to your daily energy goal",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { state.autoSetEnergy() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Fix it")
                }
            }
        }
    }
}

@Composable
private fun AutoCalculateCard(state: MacroWeightInputFormState, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Energy is calculated automatically based on macronutrients",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(
                onClick = {
                    state.autoCalculateEnergy = false
                }
            ) {
                Text("I want to set energy manually")
            }
        }
    }
}

@Composable
private fun MacroInput(state: MacroWeightInputFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.proteins.TextField(
            label = stringResource(Res.string.nutriment_proteins),
            color = nutrientsPalette.proteinsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        state.carbohydrates.TextField(
            label = stringResource(Res.string.nutriment_carbohydrates),
            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        state.fats.TextField(
            label = stringResource(Res.string.nutriment_fats),
            color = nutrientsPalette.fatsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.autoCalculateEnergy) {
            val str = buildString {
                append("=")
                append(" ${state.energy.value} ")
                append(stringResource(Res.string.unit_kcal))
            }

            Text(
                text = str,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        } else {
            OutlinedTextField(
                state = state.energy.textFieldState,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.unit_energy)) },
                suffix = { Text(stringResource(Res.string.unit_kcal)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { state.autoCalculateEnergy = true }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Undo,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun FormField<Float, DailyGoalsFormFieldError>.TextField(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    suffix: String = stringResource(Res.string.unit_gram_short),
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        ),
        isError = error != null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = color,
            focusedLabelColor = color,
            unfocusedLabelColor = color
        )
    )
}
