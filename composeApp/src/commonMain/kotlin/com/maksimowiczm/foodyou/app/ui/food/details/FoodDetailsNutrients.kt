package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts

@Composable
internal fun FoodDetailsNutrients(
    nutritionFacts: NutritionFacts,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandingEnabled: Boolean,
) {
    NutrientsHeader(
        proteins = nutritionFacts.proteins.value?.toFloat(),
        carbohydrates = nutritionFacts.carbohydrates.value?.toFloat(),
        fats = nutritionFacts.fats.value?.toFloat(),
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        enabled = expandingEnabled,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
    )
    Spacer(Modifier.height(8.dp))
    NutrientList(
        facts = nutritionFacts,
        expanded = expanded,
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { onExpandedChange(!expanded) },
                ),
    )
}
