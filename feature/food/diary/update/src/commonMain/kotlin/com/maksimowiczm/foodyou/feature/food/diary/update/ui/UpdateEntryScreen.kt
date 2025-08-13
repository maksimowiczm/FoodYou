package com.maksimowiczm.foodyou.feature.food.diary.update.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.ChipsDatePicker
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.ChipsMealPicker
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.FoodMeasurementFormState
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.Source
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.rememberFoodMeasurementFormState
import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.UpdateEntryEvent
import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.UpdateEntryViewModel
import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.canUnpack
import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.food.diary.update.presentation.suggestions
import com.maksimowiczm.foodyou.feature.food.shared.ui.MeasurementPicker
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.ext.add
import com.maksimowiczm.foodyou.shared.ui.ext.minus
import com.maksimowiczm.foodyou.shared.ui.ext.plus
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_save
import foodyou.app.generated.resources.action_unpack
import foodyou.app.generated.resources.headline_note
import foodyou.app.generated.resources.headline_source
import kotlin.time.Duration.Companion.days
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateEntryScreen(
    entryId: Long,
    onBack: () -> Unit,
    onSave: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateEntryViewModel = koinViewModel { parametersOf(entryId) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            is UpdateEntryEvent.Saved -> onSave()
        }
    }

    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val entry = viewModel.entry.collectAsStateWithLifecycle().value
    val today by viewModel.today.collectAsStateWithLifecycle()

    if (entry == null) {
        // TODO loading state
    } else {

        val state =
            rememberFoodMeasurementFormState(
                today = today,
                possibleDates =
                    listOf(today.minus(1.days), today, today.plus(1.days), entry.date)
                        .distinct()
                        .sorted(),
                selectedDate = entry.date,
                meals = remember(meals) { meals.map { it.name } },
                selectedMeal =
                    remember(meals, entry) { meals.firstOrNull { it.id == entry.mealId }?.name },
                suggestions =
                    remember(entry) {
                        (listOf(entry.measurement) + entry.food.suggestions).distinct()
                    },
                possibleTypes = remember(entry) { entry.food.possibleMeasurementTypes },
                selectedMeasurement = entry.measurement,
            )

        UpdateEntryScreen(
            onBack = onBack,
            onUnpack = {
                val selectedMealId =
                    state.mealsState.selectedMeal?.let { mealName ->
                        meals.firstOrNull { it.name == mealName }?.id
                    }

                if (selectedMealId != null) {
                    viewModel.unpack(
                        measurement = state.measurementState.measurement,
                        mealId = selectedMealId,
                        date = state.dateState.selectedDate,
                    )
                }
            },
            onSave = {
                val selectedMealId =
                    state.mealsState.selectedMeal?.let { mealName ->
                        meals.firstOrNull { it.name == mealName }?.id
                    }

                if (selectedMealId != null) {
                    viewModel.save(
                        measurement = state.measurementState.measurement,
                        mealId = selectedMealId,
                        date = state.dateState.selectedDate,
                    )
                }
            },
            state = state,
            entry = entry,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdateEntryScreen(
    onBack: () -> Unit,
    onUnpack: () -> Unit,
    onSave: () -> Unit,
    state: FoodMeasurementFormState,
    entry: DiaryEntry,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topBar =
        @Composable {
            MediumTopAppBar(
                title = { Text(entry.food.name) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        }
    val fab =
        @Composable {
            Column(
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = !animatedVisibilityScope.transition.isRunning && state.isValid,
                        alignment = Alignment.BottomEnd,
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (entry.food.canUnpack) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (state.isValid) {
                                onUnpack()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.CallSplit,
                                contentDescription = null,
                            )
                        },
                        text = { Text(stringResource(Res.string.action_unpack)) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                LargeExtendedFloatingActionButton(
                    onClick = {
                        if (state.isValid) {
                            onSave()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(Res.string.action_save),
                        )
                    },
                    text = { Text(stringResource(Res.string.action_save)) },
                )
            }
        }

    Scaffold(modifier = modifier, topBar = topBar, floatingActionButton = fab) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 8.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding =
                paddingValues.add(vertical = 8.dp).let {
                    if (entry.food.canUnpack) {
                        it.add(bottom = 8.dp + 56.dp + 8.dp + 80.dp + 24.dp) // Double FAB
                    } else {
                        it.add(bottom = 80.dp + 24.dp) // FAB
                    }
                },
        ) {
            item { HorizontalDivider() }

            item {
                ChipsDatePicker(
                    state = state.dateState,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                HorizontalDivider()
                ChipsMealPicker(
                    state = state.mealsState,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                HorizontalDivider()
                MeasurementPicker(
                    state = state.measurementState,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            val food = entry.food
            if (food is DiaryFoodRecipe) {
                item {
                    val measurement = state.measurementState.measurement
                    val ingredients = food.unpack(measurement)

                    HorizontalDivider()
                    Ingredients(ingredients, Modifier.padding(8.dp))
                }
            }

            item {
                HorizontalDivider()
                NutrientList(food = food, measurement = state.measurementState.measurement)
            }

            val note = food.note
            if (note != null) {
                item {
                    HorizontalDivider()
                    Column(Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                        Text(
                            text = stringResource(Res.string.headline_note),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(text = note, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (food is DiaryFoodProduct) {
                item {
                    HorizontalDivider()
                    Column(Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                        Text(
                            text = stringResource(Res.string.headline_source),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Source(food.source)
                    }
                }
            }
        }
    }
}
