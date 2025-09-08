package com.maksimowiczm.foodyou.feature.goals.master

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.feature.shared.ui.LocalNutrientsOrder
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFactsField
import com.maksimowiczm.foodyou.shared.domain.food.get
import com.maksimowiczm.foodyou.shared.domain.food.sum
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.IncompleteFoodsList
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalsScreen(onBack: () -> Unit, epochDay: Long, modifier: Modifier = Modifier) {
    val date = LocalDate.fromEpochDays(epochDay)
    val viewModel: GoalsViewModel = koinViewModel()
    val dateFormatter = LocalDateFormatter.current

    val screenState = rememberGoalsScreenState(selectedDate = date)

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    if (showDatePicker) {
        CalendarCardDatePickerDialog(
            goalsState = screenState,
            onDismissRequest = { showDatePicker = false },
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_summary)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null)
                    }
                },
                subtitle = { Text(dateFormatter.formatDate(screenState.selectedDate)) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item {
                HorizontalPager(
                    state = screenState.pagerState,
                    beyondViewportPageCount = 3,
                    verticalAlignment = Alignment.Top,
                ) { page ->
                    val date = screenState.dateForPage(page)
                    val uiState =
                        viewModel.observeUiStateByDate(date).collectAsStateWithLifecycle().value

                    if (uiState == null) {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            ContainedLoadingIndicator()
                        }
                    } else {
                        GoalsPage(uiState = uiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsPage(uiState: GoalsScreenUiState, modifier: Modifier = Modifier) {
    val meals = uiState.meals
    val goals = uiState.goal

    var selectedMealsIds by rememberSaveable(meals) { mutableStateOf(meals.map { it.id }) }

    val filteredMeals =
        remember(meals, selectedMealsIds) { meals.filter { it.id in selectedMealsIds } }
    val nutritionFacts = remember(filteredMeals) { filteredMeals.map { it.nutritionFacts }.sum() }

    Column(modifier) {
        MealsFilter(
            meals = meals,
            selectedMealsIds = selectedMealsIds,
            onSelectedMealsIdsChange = { selectedMealsIds = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        NutrientList(
            nutritionFacts = nutritionFacts,
            goals = goals,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (meals.incompleteFoods.isNotEmpty()) {
            IncompleteFoodsList(
                foods = meals.incompleteFoods,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun MealsFilter(
    meals: List<MealModel>,
    selectedMealsIds: List<Long>,
    onSelectedMealsIdsChange: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        meals.forEachIndexed { i, meal ->
            val selected = meal.id in selectedMealsIds

            key(meal.id) {
                FilterChip(
                    selected = selected,
                    onClick = {
                        val selectedMealsIds =
                            if (selected) {
                                selectedMealsIds - meal.id
                            } else {
                                selectedMealsIds + meal.id
                            }
                        onSelectedMealsIdsChange(selectedMealsIds)
                    },
                    label = { Text(meal.name) },
                    modifier = Modifier.animatePlacement(),
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NutrientList(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier,
) {
    val order = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        NutrientGoal(
            label = stringResource(Res.string.unit_energy),
            target =
                NutrientGoalDefaults.energyTargetString(
                    value = nutritionFacts[NutritionFactsField.Energy],
                    target = goals[NutritionFactsField.Energy],
                ),
            color = MaterialTheme.colorScheme.primary,
            state =
                rememberNutrientGoalState(
                    nutritionFacts[NutritionFactsField.Energy],
                    goals[NutritionFactsField.Energy],
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        order.forEach {
            when (it) {
                NutrientsOrder.Proteins -> Proteins(nutritionFacts, goals)
                NutrientsOrder.Fats -> Fats(nutritionFacts, goals)
                NutrientsOrder.Carbohydrates -> Carbohydrates(nutritionFacts, goals)
                NutrientsOrder.Other -> Other(nutritionFacts, goals)
                NutrientsOrder.Vitamins -> Vitamins(nutritionFacts, goals)
                NutrientsOrder.Minerals -> Minerals(nutritionFacts, goals)
            }
        }
    }
}

@Composable
private fun Proteins(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.Proteins,
            value = nutritionFacts.proteins,
            target = goals[NutritionFactsField.Proteins],
            color = nutrientsPalette.proteinsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Fats(nutritionFacts: NutritionFacts, goals: DailyGoal, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.Fats,
            value = nutritionFacts.fats,
            target = goals[NutritionFactsField.Fats],
            color = nutrientsPalette.fatsOnSurfaceContainer,
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.SaturatedFats,
            value = nutritionFacts.saturatedFats,
            target = goals[NutritionFactsField.SaturatedFats],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.TransFats,
            value = nutritionFacts.transFats,
            target = goals[NutritionFactsField.TransFats],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.MonounsaturatedFats,
            value = nutritionFacts.monounsaturatedFats,
            target = goals[NutritionFactsField.MonounsaturatedFats],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.PolyunsaturatedFats,
            value = nutritionFacts.polyunsaturatedFats,
            target = goals[NutritionFactsField.PolyunsaturatedFats],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.Omega3,
            value = nutritionFacts.omega3,
            target = goals[NutritionFactsField.Omega3],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.Omega6,
            value = nutritionFacts.omega6,
            target = goals[NutritionFactsField.Omega6],
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Carbohydrates(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.Carbohydrates,
            value = nutritionFacts.carbohydrates,
            target = goals[NutritionFactsField.Carbohydrates],
            modifier = Modifier.fillMaxWidth(),
            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
        )
        NutrientGoal(
            field = NutritionFactsField.Sugars,
            value = nutritionFacts.sugars,
            target = goals[NutritionFactsField.Sugars],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.AddedSugars,
            value = nutritionFacts.addedSugars,
            target = goals[NutritionFactsField.AddedSugars],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.DietaryFiber,
            value = nutritionFacts.dietaryFiber,
            target = goals[NutritionFactsField.DietaryFiber],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.SolubleFiber,
            value = nutritionFacts.solubleFiber,
            target = goals[NutritionFactsField.SolubleFiber],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.InsolubleFiber,
            value = nutritionFacts.insolubleFiber,
            target = goals[NutritionFactsField.InsolubleFiber],
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Other(nutritionFacts: NutritionFacts, goals: DailyGoal, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.Salt,
            value = nutritionFacts.salt,
            target = goals[NutritionFactsField.Salt],
            modifier = Modifier.fillMaxWidth(),
        )
        NutrientGoal(
            field = NutritionFactsField.Cholesterol,
            value = nutritionFacts.cholesterol * 1000.0,
            target = (goals[NutritionFactsField.Cholesterol] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Caffeine,
            value = nutritionFacts.caffeine * 1000.0,
            target = (goals[NutritionFactsField.Caffeine] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
    }
}

@Composable
private fun Vitamins(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.VitaminA,
            value = nutritionFacts.vitaminA * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminA] * 1_000_000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB1,
            value = nutritionFacts.vitaminB1 * 1000.0,
            target = (goals[NutritionFactsField.VitaminB1] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB2,
            value = nutritionFacts.vitaminB2 * 1000.0,
            target = (goals[NutritionFactsField.VitaminB2] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB3,
            value = nutritionFacts.vitaminB3 * 1000.0,
            target = (goals[NutritionFactsField.VitaminB3] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB5,
            value = nutritionFacts.vitaminB5 * 1000.0,
            target = (goals[NutritionFactsField.VitaminB5] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB6,
            value = nutritionFacts.vitaminB6 * 1000.0,
            target = (goals[NutritionFactsField.VitaminB6] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB7,
            value = nutritionFacts.vitaminB7 * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminB7] * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB9,
            value = nutritionFacts.vitaminB9 * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminB9] * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminB12,
            value = nutritionFacts.vitaminB12 * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminB12] * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminC,
            value = nutritionFacts.vitaminC * 1000.0,
            target = (goals[NutritionFactsField.VitaminC] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminD,
            value = nutritionFacts.vitaminD * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminD] * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminE,
            value = nutritionFacts.vitaminE * 1000.0,
            target = (goals[NutritionFactsField.VitaminE] * 1000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.VitaminK,
            value = nutritionFacts.vitaminK * 1_000_000.0,
            target = (goals[NutritionFactsField.VitaminK] * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
    }
}

@Composable
private fun Minerals(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NutrientGoal(
            field = NutritionFactsField.Manganese,
            value = nutritionFacts.manganese * 1000.0,
            target = (goals[NutritionFactsField.Manganese] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Magnesium,
            value = nutritionFacts.magnesium * 1000.0,
            target = (goals[NutritionFactsField.Magnesium] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Potassium,
            value = nutritionFacts.potassium * 1000.0,
            target = (goals[NutritionFactsField.Potassium] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Calcium,
            value = nutritionFacts.calcium * 1000.0,
            target = (goals[NutritionFactsField.Calcium] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Copper,
            value = nutritionFacts.copper * 1000.0,
            target = (goals[NutritionFactsField.Copper] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Zinc,
            value = nutritionFacts.zinc * 1000.0,
            target = (goals[NutritionFactsField.Zinc] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Sodium,
            value = nutritionFacts.sodium * 1000.0,
            target = (goals[NutritionFactsField.Sodium] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Iron,
            value = nutritionFacts.iron * 1000.0,
            target = (goals[NutritionFactsField.Iron] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Phosphorus,
            value = nutritionFacts.phosphorus * 1000.0,
            target = (goals[NutritionFactsField.Phosphorus] * 1000),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_milligram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Selenium,
            value = nutritionFacts.selenium * 1_000_000.0,
            target = (goals[NutritionFactsField.Selenium] * 1_000_000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Iodine,
            value = nutritionFacts.iodine * 1_000_000.0,
            target = (goals[NutritionFactsField.Iodine] * 1_000_000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
        NutrientGoal(
            field = NutritionFactsField.Chromium,
            value = nutritionFacts.chromium * 1_000_000.0,
            target = (goals[NutritionFactsField.Chromium] * 1_000_000.0),
            modifier = Modifier.fillMaxWidth(),
            suffix = stringResource(Res.string.unit_microgram_short),
        )
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
private fun CalendarCardDatePickerDialog(
    goalsState: GoalsScreenState,
    onDismissRequest: () -> Unit,
) {
    val state = goalsState.rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        goalsState.goToDate(
                            date =
                                Instant.fromEpochMilliseconds(it)
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                        )
                    }
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(Res.string.positive_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.action_cancel))
            }
        },
    ) {
        DatePicker(
            state = state,
            title = {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DatePickerDefaults.DatePickerTitle(displayMode = state.displayMode)

                    TextButton(
                        onClick = {
                            goalsState.goToToday()
                            onDismissRequest()
                        }
                    ) {
                        Text(stringResource(Res.string.action_go_to_today))
                    }
                }
            },
            // It won't fit on small screens, so we need to scroll
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}
