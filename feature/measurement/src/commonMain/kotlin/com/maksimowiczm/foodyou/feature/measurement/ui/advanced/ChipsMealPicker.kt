package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ChipsMealPicker(
    meals: List<String>,
    selectedMeal: Int?,
    onMealSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            meals.forEachIndexed { i, meal ->
                InputChip(
                    selected = selectedMeal == i,
                    onClick = { onMealSelect(i) },
                    label = { Text(meal) }
                )
            }
        }
    }
}
