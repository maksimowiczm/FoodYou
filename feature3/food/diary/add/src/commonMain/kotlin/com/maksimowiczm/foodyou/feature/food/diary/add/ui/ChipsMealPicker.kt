package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ChipsMealPicker(state: ChipsMealPickerState, modifier: Modifier = Modifier) {
    Row(modifier) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.Restaurant, contentDescription = null)
        }

        Spacer(Modifier.width(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.meals.forEachIndexed { i, meal ->
                InputChip(
                    selected = meal == state.selectedMeal,
                    onClick = { state.selectedMeal = meal },
                    label = { Text(meal) },
                )
            }
        }
    }
}

@Composable
internal fun rememberChipsMealPickerState(
    meals: List<String>,
    selectedMeal: String?,
): ChipsMealPickerState {
    return rememberSaveable(
        meals,
        selectedMeal,
        saver =
            Saver(
                save = { listOf(it.selectedMeal, it.meals) },
                restore = {
                    @Suppress("UNCHECKED_CAST")
                    ChipsMealPickerState(
                        initialMeals = it[1] as List<String>,
                        selectedMeal = it[0] as String?,
                    )
                },
            ),
    ) {
        ChipsMealPickerState(initialMeals = meals, selectedMeal = selectedMeal)
    }
}

@Stable
internal class ChipsMealPickerState(initialMeals: List<String>, selectedMeal: String?) {
    init {
        require(initialMeals.isNotEmpty()) { "Meals list cannot be empty" }
    }

    val meals = initialMeals

    var selectedMeal by mutableStateOf(selectedMeal)
}
