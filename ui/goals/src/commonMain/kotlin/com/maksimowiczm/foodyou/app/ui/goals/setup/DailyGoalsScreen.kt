package com.maksimowiczm.foodyou.app.ui.goals.setup

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.shared.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.shared.component.DiscardDialog
import com.maksimowiczm.foodyou.app.ui.shared.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.shared.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.compose.component.BackHandler
import com.maksimowiczm.foodyou.shared.compose.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.compose.extension.add
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.utility.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalsScreen(onBack: () -> Unit, onSave: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: DailyGoalsViewModel = koinViewModel()
    val weeklyGoals = viewModel.weeklyGoals.collectAsStateWithLifecycle().value

    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            DailyGoalsViewModelEvent.Updated -> onSave()
        }
    }

    if (weeklyGoals == null) {
        // TODO loading state
        return
    }

    val state = rememberWeeklyGoalsState(weeklyGoals)

    DailyGoalsContent(
        weeklyState = state,
        onBack = onBack,
        onSave = {
            val weeklyGoals = state.intoWeeklyGoals()
            viewModel.updateWeeklyGoals(weeklyGoals)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DailyGoalsContent(
    weeklyState: WeeklyGoalsState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier,
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val handleOnBack = { if (weeklyState.isModified) showDiscardDialog = true else onBack() }
    BackHandler(weeklyState.isModified) { showDiscardDialog = true }
    if (showDiscardDialog) {
        DiscardDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(handleOnBack) },
                actions = {
                    FilledIconButton(
                        onClick = onSave,
                        shapes = IconButtonDefaults.shapes(),
                        enabled = weeklyState.isValid,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = stringResource(Res.string.action_save),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                DayPicker(
                    useSeparateGoals = weeklyState.useSeparateGoals,
                    onUseSeparateGoalsChange = { weeklyState.useSeparateGoals = it },
                    selectedDay = weeklyState.selectedDay,
                    onSelectedDayChange = { weeklyState.selectedDay = it },
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                val state = weeklyState.selectedDayGoals

                Column(modifier) {
                    Text(
                        text = stringResource(Res.string.action_set_goals),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    WeightOrPercentageToggle(
                        useDistribution = state.inputType == InputType.Percentage,
                        onUseDistributionChange = {
                            state.inputType = if (it) InputType.Percentage else InputType.Weight
                        },
                        modifier =
                            Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                    )
                    if (state.inputType == InputType.Percentage) {
                        MacroInputSliderForm(
                            state = state,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    } else {
                        MacroInput(
                            state = state,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    AdditionalGoalsForm(
                        state = state.additionalState,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DayPicker(
    useSeparateGoals: Boolean,
    onUseSeparateGoalsChange: (Boolean) -> Unit,
    selectedDay: Int,
    onSelectedDayChange: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_pick_the_days),
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clickable { onUseSeparateGoalsChange(!useSeparateGoals) }
                    .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Checkbox(
                modifier = Modifier.padding(vertical = 16.dp),
                checked = useSeparateGoals,
                onCheckedChange = null,
            )
            Text(
                text = stringResource(Res.string.action_set_separate_goals),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        AnimatedVisibility(useSeparateGoals) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                contentPadding = contentPadding,
            ) {
                item {
                    val dateFormatter = LocalDateFormatter.current
                    val weekDayNamesShort = dateFormatter.weekDayNamesShort

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                ButtonGroupDefaults.ConnectedSpaceBetween,
                                Alignment.CenterHorizontally,
                            )
                    ) {
                        weekDayNamesShort.forEachIndexed { i, name ->
                            ToggleButton(
                                checked = selectedDay == i,
                                onCheckedChange = {
                                    onSelectedDayChange(i)
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.SegmentTick
                                    )
                                },
                                modifier = Modifier.semantics { role = Role.RadioButton },
                                shapes =
                                    when (i) {
                                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        weekDayNamesShort.lastIndex ->
                                            ButtonGroupDefaults.connectedTrailingButtonShapes()

                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    },
                            ) {
                                Text(name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WeightOrPercentageToggle(
    useDistribution: Boolean,
    onUseDistributionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                ButtonGroupDefaults.ConnectedSpaceBetween,
                Alignment.CenterHorizontally,
            ),
    ) {
        ToggleButton(
            checked = !useDistribution,
            onCheckedChange = { onUseDistributionChange(false) },
            modifier = Modifier.height(56.dp).semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
        ) {
            Icon(painter = painterResource(Res.drawable.ic_weight), contentDescription = null)
            Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.weight))
        }
        ToggleButton(
            checked = useDistribution,
            onCheckedChange = { onUseDistributionChange(true) },
            modifier = Modifier.height(56.dp).semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
        ) {
            Icon(imageVector = Icons.Outlined.Percent, contentDescription = null)
            Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.headline_percentages))
        }
    }
}

@Composable
private fun MacroInputSliderForm(state: DailyGoalsFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            state = state.energy.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.unit_energy)) },
            suffix = { Text(stringResource(Res.string.unit_kcal)) },
            isError = state.energy.error != null,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )

        nutrientsOrder.forEach {
            when (it) {
                NutrientsOrder.Proteins ->
                    MacroSlider(
                        value = state.proteinsSlider,
                        onValueChange = { state.proteinsSlider = it },
                        color = nutrientsPalette.proteinsOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_proteins),
                    )

                NutrientsOrder.Fats ->
                    MacroSlider(
                        value = state.fatsSlider,
                        onValueChange = { state.fatsSlider = it },
                        color = nutrientsPalette.fatsOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_fats),
                    )

                NutrientsOrder.Carbohydrates ->
                    MacroSlider(
                        value = state.carbsSlider,
                        onValueChange = { state.carbsSlider = it },
                        color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_carbohydrates),
                    )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }
    }
}

@Composable
private fun MacroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = color, style = MaterialTheme.typography.bodyMedium)
            Text(
                text =
                    buildString {
                        append(value.roundToInt())
                        append("%")
                    },
                color = color,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Slider(
            value = value,
            onValueChange = { onValueChange(it.roundToInt().toFloat()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(activeTrackColor = color, thumbColor = color),
        )
    }
}

@Composable
private fun MacroInput(state: DailyGoalsFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        nutrientsOrder.forEach {
            when (it) {
                NutrientsOrder.Proteins ->
                    state.proteins.TextField(
                        label = stringResource(Res.string.nutriment_proteins),
                        color = nutrientsPalette.proteinsOnSurfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    )

                NutrientsOrder.Fats ->
                    state.fats.TextField(
                        label = stringResource(Res.string.nutriment_fats),
                        color = nutrientsPalette.fatsOnSurfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    )

                NutrientsOrder.Carbohydrates ->
                    state.carbs.TextField(
                        label = stringResource(Res.string.nutriment_carbohydrates),
                        color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        modifier = Modifier.fillMaxWidth(),
                    )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }

        val str = buildString {
            append("=")
            append(" ${state.energy.value.roundToInt()} ")
            append(stringResource(Res.string.unit_kcal))
        }

        Text(
            text = str,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
private fun FormField<Double, DailyGoalsFormError>.TextField(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    suffix: String = stringResource(Res.string.unit_gram_short),
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
        isError = error != null,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = color,
                focusedLabelColor = color,
                unfocusedLabelColor = color,
            ),
    )
}
