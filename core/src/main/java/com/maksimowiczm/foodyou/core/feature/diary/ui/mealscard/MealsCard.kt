package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.core.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.core.ui.toggle
import kotlinx.datetime.LocalTime

@Composable
fun MealsCard(
    diaryDay: DiaryDay,
    time: LocalTime,
    onAddClick: (Meal) -> Unit,
    onEditMeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (diaryDay.meals.isEmpty()) {
        EmptyMealsCard(
            onEditMeals = onEditMeals,
            modifier = modifier
        )
    } else {
        var expanded by rememberSaveable { mutableStateOf(false) }

        MealsCard(
            diaryDay = diaryDay,
            time = time,
            onAddClick = onAddClick,
            expanded = expanded,
            onExpandChange = { expanded = it },
            onEditMeals = onEditMeals,
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyMealsCard(
    onEditMeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.neutral_seems_like_you_haven_t_added_any_meals_yet)
            )
            OutlinedButton(
                onClick = onEditMeals
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.action_add_your_first_meal)
                )
            }
        }
    }
}

@Composable
private fun MealsCard(
    diaryDay: DiaryDay,
    time: LocalTime,
    onAddClick: (Meal) -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onEditMeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val hapticOnExpandChange: (Boolean) -> Unit = {
        onExpandChange(it)
        hapticFeedback.toggle(it)
    }

    val lastVisibleIndex by remember(diaryDay, time, expanded) {
        derivedStateOf {
            if (expanded) {
                diaryDay.meals.size - 1
            } else {
                diaryDay.meals.indexOfLast { meal -> shouldShowMeal(meal, time) }
            }
        }
    }

    val empty = lastVisibleIndex == -1

    ElevatedCard(
        modifier = modifier.animateContentSize(),
        onClick = { hapticOnExpandChange(!expanded) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = empty && !expanded
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.neutral_no_meals_to_show_at_this_time),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            diaryDay.meals.forEachIndexed { index, meal ->
                AnimatedVisibility(
                    visible = if (expanded) true else shouldShowMeal(meal, time)
                ) {
                    Column {
                        MaterialMealItem(
                            icon = {},
                            title = {
                                Text(
                                    text = meal.name
                                )
                            },
                            value = diaryDay.totalCalories(meal),
                            onAddClick = { onAddClick(meal) },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (index < lastVisibleIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            if (expanded) {
                TextButton(
                    onClick = onEditMeals
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_edit_meals)
                    )
                }
            }
        }
    }
}

/**
 * Determines whether a meal should be shown based on the current time.
 *
 * This function handles two cases:
 * 1. Meals that span across midnight (where end time is less than start time)
 * 2. Regular meals within the same day (where start time is less than end time)
 *
 * For meals spanning midnight (e.g., from 22:00 to 04:00), the function checks if the current time:
 * - Falls between the start time and midnight (23:59), OR
 * - Falls between midnight (00:00) and the end time
 *
 * For regular meals (e.g., from 12:00 to 15:00), it simply checks if the current time
 * falls within the start and end times.
 */
private fun shouldShowMeal(meal: Meal, time: LocalTime): Boolean {
    return if (meal.to < meal.from) {
        val minuteBeforeMidnight = LocalTime(23, 59, 59)
        val midnight = LocalTime(0, 0, 0)
        meal.from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= meal.to
    } else {
        meal.from <= time && time <= meal.to
    }
}

@Composable
private fun MaterialMealItem(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    value: Int,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleLarge
            ) {
                title()
            }
        },
        modifier = modifier,
        leadingContent = icon,
        supportingContent = {
            Text(
                text = "$value " + stringResource(R.string.unit_kcal),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        },
        trailingContent = {
            FilledIconButton(
                onClick = onAddClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_product)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Preview
@Composable
private fun EmptyMealsCardPreview() {
    FoodYouTheme {
        EmptyMealsCard(
            onEditMeals = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// If you run this preview at 3:33 then it won't work, good job bro!
// (it's a joke) (the second part is a joke) (the part with "good job bro" is a joke) ( :) )
@Preview
@Composable
private fun FilteredMealsCardPreview() {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()

    val filtered = diaryDay.copy(
        mealProductMap = diaryDay.mealProductMap.map { (m, products) ->
            m.copy(
                from = LocalTime(3, 33),
                to = LocalTime(3, 33)
            ) to products
        }.toMap()
    )

    FoodYouTheme {
        MealsCard(
            diaryDay = filtered,
            time = LocalTime(12, 0),
            onAddClick = {},
            expanded = false,
            onExpandChange = {},
            onEditMeals = {}
        )
    }
}

@Preview
@Composable
private fun MealsCardPreview(
    @PreviewParameter(BooleanPreviewParameter::class) expanded: Boolean
) {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()

    FoodYouTheme {
        MealsCard(
            diaryDay = diaryDay,
            time = LocalTime(12, 0),
            onAddClick = {},
            // Reverse the expanded state for the preview
            expanded = expanded.not(),
            onExpandChange = {},
            onEditMeals = {}
        )
    }
}
