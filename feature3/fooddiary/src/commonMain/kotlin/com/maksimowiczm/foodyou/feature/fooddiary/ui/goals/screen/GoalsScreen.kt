package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.sum
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.maksimowiczm.foodyou.feature.food.ui.IncompleteFoodsList
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_summary
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import foodyou.app.generated.resources.unit_microgram_short
import foodyou.app.generated.resources.unit_milligram_short
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun GoalsScreen(
    onBack: () -> Unit,
    onFoodClick: (FoodId) -> Unit,
    date: LocalDate,
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current

    val screenState = rememberGoalsScreenState(
        zeroDate = date
    )

    val order by userPreference<NutrientsOrderPreference>()
        .collectAsStateWithLifecycleInitialBlock()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_summary)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(dateFormatter.formatDate(screenState.selectedDate)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                val meals =
                    viewModel.observeMeals(date).collectAsStateWithLifecycle().value
                val goals =
                    viewModel.observeGoals(date).collectAsStateWithLifecycle().value

                if (meals == null || goals == null) {
                    // TODO loading state
                } else {
                    GoalsPage(
                        meals = meals,
                        goals = goals,
                        order = order,
                        onFoodClick = onFoodClick
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsPage(
    meals: List<Meal>,
    goals: DailyGoal,
    order: List<NutrientsOrder>,
    onFoodClick: (FoodId) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMealsIds by rememberSaveable(meals) {
        mutableStateOf(meals.map { it.id })
    }

    val filteredMeals = remember(meals, selectedMealsIds) {
        meals.filter { it.id in selectedMealsIds }
    }
    val nutritionFacts = remember(filteredMeals) {
        filteredMeals.map { it.nutritionFacts }.sum()
    }

    Column(modifier) {
        MealsFilter(
            meals = meals,
            selectedMealsIds = selectedMealsIds,
            onSelectedMealsIdsChange = { selectedMealsIds = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        NutrientList(
            nutritionFacts = nutritionFacts,
            goals = goals,
            order = order,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (!nutritionFacts.isComplete) {
            val incomplete = filteredMeals
                .flatMap { it.food }
                .map { it.food }
                .filter { it is Product }
                .filter { !it.nutritionFacts.isComplete }

            IncompleteFoodsList(
                foods = incomplete.map { it.headline },
                onFoodClick = { foodName ->
                    val id = incomplete.firstOrNull {
                        it.headline == foodName
                    }?.id

                    if (id != null) {
                        onFoodClick(id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun MealsFilter(
    meals: List<Meal>,
    selectedMealsIds: List<Long>,
    onSelectedMealsIdsChange: (List<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meals.forEachIndexed { i, meal ->
            val selected = meal.id in selectedMealsIds

            key(meal.id) {
                FilterChip(
                    selected = selected,
                    onClick = {
                        val selectedMealsIds = if (selected) {
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
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NutrientList(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    order: List<NutrientsOrder>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Energy.stringResource(),
            value = nutritionFacts.get(NutritionFactsField.Energy).value!!,
            target = goals[NutritionFactsField.Energy].toFloat(),
            disclaimer = false,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary,
            unit = stringResource(Res.string.unit_kcal)
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
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Proteins.stringResource(),
            nutrientValue = nutritionFacts.proteins,
            target = goals[NutritionFactsField.Proteins].toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = nutrientsPalette.proteinsOnSurfaceContainer,
            trackColor = nutrientsPalette.proteinsOnSurfaceContainer
        )
    }
}

@Composable
private fun Fats(nutritionFacts: NutritionFacts, goals: DailyGoal, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Fats.stringResource(),
            nutrientValue = nutritionFacts.fats,
            target = goals[NutritionFactsField.Fats].toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = nutrientsPalette.fatsOnSurfaceContainer,
            trackColor = nutrientsPalette.fatsOnSurfaceContainer
        )
        NutrientGoal(
            label = NutritionFactsField.SaturatedFats.stringResource(),
            nutrientValue = nutritionFacts.saturatedFats,
            target = goals[NutritionFactsField.SaturatedFats].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.TransFats.stringResource(),
            nutrientValue = nutritionFacts.transFats,
            target = goals[NutritionFactsField.TransFats].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.MonounsaturatedFats.stringResource(),
            nutrientValue = nutritionFacts.monounsaturatedFats,
            target = goals[NutritionFactsField.MonounsaturatedFats].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.PolyunsaturatedFats.stringResource(),
            nutrientValue = nutritionFacts.polyunsaturatedFats,
            target = goals[NutritionFactsField.PolyunsaturatedFats].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.Omega3.stringResource(),
            nutrientValue = nutritionFacts.omega3,
            target = goals[NutritionFactsField.Omega3].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.Omega6.stringResource(),
            nutrientValue = nutritionFacts.omega6,
            target = goals[NutritionFactsField.Omega6].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Carbohydrates(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Carbohydrates.stringResource(),
            nutrientValue = nutritionFacts.carbohydrates,
            target = goals[NutritionFactsField.Carbohydrates].toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
            trackColor = nutrientsPalette.carbohydratesOnSurfaceContainer
        )
        NutrientGoal(
            label = NutritionFactsField.Sugars.stringResource(),
            nutrientValue = nutritionFacts.sugars,
            target = goals[NutritionFactsField.Sugars].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.AddedSugars.stringResource(),
            nutrientValue = nutritionFacts.addedSugars,
            target = goals[NutritionFactsField.AddedSugars].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.DietaryFiber.stringResource(),
            nutrientValue = nutritionFacts.dietaryFiber,
            target = goals[NutritionFactsField.DietaryFiber].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.SolubleFiber.stringResource(),
            nutrientValue = nutritionFacts.solubleFiber,
            target = goals[NutritionFactsField.SolubleFiber].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.InsolubleFiber.stringResource(),
            nutrientValue = nutritionFacts.insolubleFiber,
            target = goals[NutritionFactsField.InsolubleFiber].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Other(nutritionFacts: NutritionFacts, goals: DailyGoal, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Salt.stringResource(),
            nutrientValue = nutritionFacts.salt,
            target = goals[NutritionFactsField.Salt].toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        NutrientGoal(
            label = NutritionFactsField.Cholesterol.stringResource(),
            nutrientValue = nutritionFacts.cholesterolMilli,
            target = (goals[NutritionFactsField.Cholesterol].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Caffeine.stringResource(),
            nutrientValue = nutritionFacts.caffeineMilli,
            target = (goals[NutritionFactsField.Caffeine].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
    }
}

@Composable
private fun Vitamins(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.VitaminA.stringResource(),
            nutrientValue = nutritionFacts.vitaminAMicro,
            target = (goals[NutritionFactsField.VitaminA] * 1000_000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB1.stringResource(),
            nutrientValue = nutritionFacts.vitaminB1Milli,
            target = (goals[NutritionFactsField.VitaminB1].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB2.stringResource(),
            nutrientValue = nutritionFacts.vitaminB2Milli,
            target = (goals[NutritionFactsField.VitaminB2].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB3.stringResource(),
            nutrientValue = nutritionFacts.vitaminB3Milli,
            target = (goals[NutritionFactsField.VitaminB3].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB5.stringResource(),
            nutrientValue = nutritionFacts.vitaminB5Milli,
            target = (goals[NutritionFactsField.VitaminB5].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB6.stringResource(),
            nutrientValue = nutritionFacts.vitaminB6Milli,
            target = (goals[NutritionFactsField.VitaminB6].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB7.stringResource(),
            nutrientValue = nutritionFacts.vitaminB7Micro,
            target = (goals[NutritionFactsField.VitaminB7].toFloat() * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB9.stringResource(),
            nutrientValue = nutritionFacts.vitaminB9Micro,
            target = (goals[NutritionFactsField.VitaminB9].toFloat() * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminB12.stringResource(),
            nutrientValue = nutritionFacts.vitaminB12Micro,
            target = (goals[NutritionFactsField.VitaminB12].toFloat() * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminC.stringResource(),
            nutrientValue = nutritionFacts.vitaminCMilli,
            target = (goals[NutritionFactsField.VitaminC].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminD.stringResource(),
            nutrientValue = nutritionFacts.vitaminDMicro,
            target = (goals[NutritionFactsField.VitaminD].toFloat() * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminE.stringResource(),
            nutrientValue = nutritionFacts.vitaminEMilli,
            target = (goals[NutritionFactsField.VitaminE].toFloat() * 1000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.VitaminK.stringResource(),
            nutrientValue = nutritionFacts.vitaminKMicro,
            target = (goals[NutritionFactsField.VitaminK].toFloat() * 1000_000f),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
    }
}

@Composable
private fun Minerals(
    nutritionFacts: NutritionFacts,
    goals: DailyGoal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientGoal(
            label = NutritionFactsField.Manganese.stringResource(),
            nutrientValue = nutritionFacts.manganeseMilli,
            target = (goals[NutritionFactsField.Manganese] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Magnesium.stringResource(),
            nutrientValue = nutritionFacts.magnesiumMilli,
            target = (goals[NutritionFactsField.Magnesium] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Potassium.stringResource(),
            nutrientValue = nutritionFacts.potassiumMilli,
            target = (goals[NutritionFactsField.Potassium] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Calcium.stringResource(),
            nutrientValue = nutritionFacts.calciumMilli,
            target = (goals[NutritionFactsField.Calcium] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Copper.stringResource(),
            nutrientValue = nutritionFacts.copperMilli,
            target = (goals[NutritionFactsField.Copper] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Zinc.stringResource(),
            nutrientValue = nutritionFacts.zincMilli,
            target = (goals[NutritionFactsField.Zinc] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Sodium.stringResource(),
            nutrientValue = nutritionFacts.sodiumMilli,
            target = (goals[NutritionFactsField.Sodium] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Iron.stringResource(),
            nutrientValue = nutritionFacts.ironMilli,
            target = (goals[NutritionFactsField.Iron] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Phosphorus.stringResource(),
            nutrientValue = nutritionFacts.phosphorusMilli,
            target = (goals[NutritionFactsField.Phosphorus] * 1000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_milligram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Selenium.stringResource(),
            nutrientValue = nutritionFacts.seleniumMicro,
            target = (goals[NutritionFactsField.Selenium] * 1000_000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Iodine.stringResource(),
            nutrientValue = nutritionFacts.iodineMicro,
            target = (goals[NutritionFactsField.Iodine] * 1000_000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
        NutrientGoal(
            label = NutritionFactsField.Chromium.stringResource(),
            nutrientValue = nutritionFacts.chromiumMicro,
            target = (goals[NutritionFactsField.Chromium] * 1000_000).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            unit = stringResource(Res.string.unit_microgram_short)
        )
    }
}

@Composable
private fun NutrientGoal(
    label: String,
    nutrientValue: NutrientValue,
    target: Float,
    modifier: Modifier = Modifier,
    unit: String = stringResource(Res.string.unit_gram_short),
    color: Color = LocalContentColor.current,
    trackColor: Color = MaterialTheme.colorScheme.outline
) {
    NutrientGoal(
        label = label,
        value = nutrientValue.value!!,
        target = target,
        disclaimer = !nutrientValue.isComplete,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
        unit = unit
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NutrientGoal(
    label: String,
    value: Float,
    target: Float,
    disclaimer: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    trackColor: Color = MaterialTheme.colorScheme.outline,
    unit: String = stringResource(Res.string.unit_gram_short)
) {
    val isExceeded = remember(value, target) {
        value > target
    }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val valueString =
        remember(colorScheme, typography, value, target, unit, disclaimer, isExceeded) {
            buildAnnotatedString {
                val color = if (isExceeded) {
                    colorScheme.error
                } else {
                    colorScheme.onSurface
                }
                val labelStyle = typography.bodyLarge.copy(
                    color = color
                )

                withStyle(labelStyle.toSpanStyle()) {
                    if (disclaimer) {
                        append("* ")
                    }

                    append(value.formatClipZeros())
                }

                val targetStyle = typography.bodyLarge.copy(
                    color = colorScheme.outline
                )

                withStyle(targetStyle.toSpanStyle()) {
                    append(" / ")
                    append(target.formatClipZeros())
                    append(" $unit")
                }
            }
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (isExceeded) MaterialTheme.colorScheme.error else color
            )
            Text(
                text = valueString,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        val progress by animateFloatAsState(
            targetValue = value / target.coerceAtLeast(.01f),
            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
        )
        val trackColor by animateColorAsState(
            if (progress > 1) MaterialTheme.colorScheme.error else trackColor
        )

        LinearProgressIndicator(
            progress = { progress % 1f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = trackColor,
            trackColor = trackColor.copy(alpha = 0.25f),
            drawStopIndicator = {}
        )
    }
}
