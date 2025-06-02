package com.maksimowiczm.foodyou.feature.goals.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Meal
import com.maksimowiczm.foodyou.core.ui.modifier.animatePlacement

@Composable
internal fun MealsFilter(state: MealsFilterState, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.meals.forEach { meal ->
            val selected by remember(state.selectedMeals) {
                derivedStateOf { meal.id in state.selectedMeals }
            }

            val icon = @Composable {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                        .testTag("${MealsFilterTestTags.MealChipIcon(meal)}")
                )
            }

            key(meal.id) {
                FilterChip(
                    selected = selected,
                    onClick = {
                        state.onClick(meal.id)
                    },
                    label = { Text(meal.name) },
                    leadingIcon = if (selected) icon else null,
                    modifier = Modifier
                        .testTag("${MealsFilterTestTags.MealChip(meal)}")
                        .animatePlacement()
                )
            }
        }
    }
}

@Composable
internal fun rememberMealsFilterState(meals: Set<Meal>): MealsFilterState = rememberSaveable(
    saver = Saver<MealsFilterState, List<Long>>(
        save = {
            it.selectedMeals.toList()
        },
        restore = {
            MealsFilterState(
                meals = meals,
                initialSelectedMeals = it
            )
        }
    )
) {
    MealsFilterState(
        meals = meals,
        initialSelectedMeals = meals.map { it.id }
    )
}

@Stable
internal class MealsFilterState(val meals: Set<Meal>, initialSelectedMeals: List<Long>) {
    var selectedMeals by mutableStateOf(initialSelectedMeals)
        private set

    fun onClick(id: Long) {
        selectedMeals = if (id in selectedMeals) {
            selectedMeals - id
        } else {
            selectedMeals + id
        }
    }
}

internal object MealsFilterTestTags {
    data class MealChip(val meal: Meal)
    data class MealChipIcon(val meal: Meal)
}
