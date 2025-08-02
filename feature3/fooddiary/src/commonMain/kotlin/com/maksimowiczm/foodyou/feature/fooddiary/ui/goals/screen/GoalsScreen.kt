package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.food.domain.sum
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GoalsScreen(
    onBack: () -> Unit,
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val date by viewModel.date.collectAsStateWithLifecycle()
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val goals = viewModel.goals.collectAsStateWithLifecycle().value

    if (meals == null || goals == null) {
        // TODO loading state
    } else {
        GoalsScreen(
            onBack = onBack,
            date = date,
            meals = meals,
            goals = goals,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsScreen(
    onBack: () -> Unit,
    date: LocalDate,
    meals: List<Meal>,
    goals: DailyGoal,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current

    var selectedMealsIds by rememberSaveable(meals) {
        mutableStateOf(meals.map { it.id })
    }

    val filteredMeals = remember(meals, selectedMealsIds) {
        meals.filter { it.id in selectedMealsIds }
    }
    val nutritionFacts = remember(filteredMeals) {
        filteredMeals.map { it.nutritionFacts }.sum()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_summary)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(dateFormatter.formatDate(date)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                MealsFilter(
                    meals = meals,
                    selectedMealsIds = selectedMealsIds,
                    onSelectedMealsIdsChange = { selectedMealsIds = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                NutrientGoal(
                    label = "Energy",
                    value = nutritionFacts.get(NutritionFactsField.Energy).value!!,
                    target = goals[NutritionFactsField.Energy].toFloat(),
                    disclaimer = false,
                    unit = "kcal",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )
            }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun NutrientGoal(
    label: String,
    value: Float,
    target: Float,
    disclaimer: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    unit: String = stringResource(Res.string.unit_gram_short)
) {
    val isExceeded = remember(value, target) {
        value > target
    }
    val color by animateColorAsState(if (isExceeded) MaterialTheme.colorScheme.error else color)

    val progress by animateFloatAsState(
        targetValue = if (value > target) {
            ((value - target) / target).coerceIn(0f, 1f)
        } else {
            value / target
        },
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

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
                color = color
            )
            Text(
                text = valueString,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.25f),
            drawStopIndicator = {}
        )
    }
}
