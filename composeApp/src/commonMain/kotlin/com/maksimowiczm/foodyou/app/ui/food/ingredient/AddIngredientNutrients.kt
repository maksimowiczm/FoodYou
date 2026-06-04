package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.getOrNull

@Composable
fun AddIngredientNutrients(
    nutritionFacts: NutritionFacts,
    quantities: List<Quantity>,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
    onSelectQuantity: (Quantity) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandingEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val stringedQuantities = quantities.mapNotNull { quantity ->
        quantity.stringResource(packageQuantity, servingQuantity).getOrNull()?.let {
            quantity to it
        }
    }

    Column(modifier) {
        if (quantities.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(stringedQuantities) { (quantity, text) ->
                    AssistChip(onClick = { onSelectQuantity(quantity) }, label = { Text(text) })
                }
            }
        }
        if (nutritionFacts.hasMacronutrientsValues()) {
            NutrientsHeader(
                proteins = nutritionFacts.proteins.value?.grams?.toFloat(),
                carbohydrates = nutritionFacts.carbohydrates.value?.grams?.toFloat(),
                fats = nutritionFacts.fats.value?.grams?.toFloat(),
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                enabled = expandingEnabled,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            )
        }
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
}
