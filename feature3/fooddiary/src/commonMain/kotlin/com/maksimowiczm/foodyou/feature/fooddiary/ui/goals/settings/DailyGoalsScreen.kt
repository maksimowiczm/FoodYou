package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transformLatest
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyGoalsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val goalsPreference = userPreference<GoalsPreference>()
    val weeklyGoals by goalsPreference.collectAsStateWithLifecycleInitialBlock()

    val state = rememberDailyGoalsFormState(weeklyGoals.monday)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(onBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                DailyGoalsForm(
                    formState = state,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun DailyGoalsForm(
    formState: DailyGoalsFormState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = "Pick the days",
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // TODO
                }
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Checkbox(
                modifier = Modifier.padding(
                    vertical = 16.dp
                ),
                checked = false,
                onCheckedChange = null
            )
            Text(
                text = "Set separate goals for each day",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Set goals",
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        AnimatedVisibility(
            visible = formState.autoCalculateEnergy,
            modifier = Modifier.padding(contentPadding)
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                AutoCalculateCard(
                    formState = formState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        var badEnergy by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(formState) {
            snapshotFlow { formState.badEnergy }.transformLatest {
                if (it) {
                    delay(1.seconds)
                }

                emit(it)
            }.collectLatest {
                badEnergy = it
            }
        }
        AnimatedVisibility(
            visible = badEnergy,
            modifier = Modifier.padding(contentPadding)
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                BadEnergyCard(
                    formState = formState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        MacroInput(
            formState = formState,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun BadEnergyCard(formState: DailyGoalsFormState, modifier: Modifier = Modifier) {
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
                    onClick = { formState.autoSetEnergy() },
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
private fun AutoCalculateCard(formState: DailyGoalsFormState, modifier: Modifier = Modifier) {
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
                    formState.autoCalculateEnergy = false
                }
            ) {
                Text("I want to set energy manually")
            }
        }
    }
}

@Composable
private fun MacroInput(formState: DailyGoalsFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        formState.proteins.TextField(
            label = stringResource(Res.string.nutriment_proteins),
            color = nutrientsPalette.proteinsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        formState.carbohydrates.TextField(
            label = stringResource(Res.string.nutriment_carbohydrates),
            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        formState.fats.TextField(
            label = stringResource(Res.string.nutriment_fats),
            color = nutrientsPalette.fatsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth()
        )
        EnergyTextField(
            formState = formState,
            modifier = Modifier.fillMaxWidth()
        )
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = color,
            focusedLabelColor = color,
            unfocusedLabelColor = color
        )
    )
}

@Composable
private fun EnergyTextField(formState: DailyGoalsFormState, modifier: Modifier = Modifier) {
    if (formState.autoCalculateEnergy) {
        val energy = NutrientsHelper.calculateEnergy(
            proteins = formState.proteins.value,
            carbohydrates = formState.carbohydrates.value,
            fats = formState.fats.value
        )

        val str = buildString {
            append("=")
            append(" ${energy.roundToInt()} ")
            append(stringResource(Res.string.unit_kcal))
        }

        Text(
            text = str,
            modifier = modifier,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
    } else {
        OutlinedTextField(
            state = formState.energy.textFieldState,
            modifier = modifier,
            label = { Text(stringResource(Res.string.unit_energy)) },
            suffix = { Text(stringResource(Res.string.unit_kcal)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconButton(
                    onClick = { formState.autoCalculateEnergy = true }
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
