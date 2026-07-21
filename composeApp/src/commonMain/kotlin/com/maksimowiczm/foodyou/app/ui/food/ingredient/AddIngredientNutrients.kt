package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.extension.horizontal
import com.maksimowiczm.foodyou.app.ui.common.extension.vertical
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
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
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
) {
    val stringedQuantities = quantities.mapNotNull { quantity ->
        quantity.stringResource(packageQuantity, servingQuantity).getOrNull()?.let {
            quantity to it
        }
    }

    Column(modifier.padding(contentPadding.vertical())) {
        if (quantities.isNotEmpty()) {
            LazyRow(
                contentPadding = contentPadding.horizontal(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(stringedQuantities) { (quantity, text) ->
                    AssistChip(onClick = { onSelectQuantity(quantity) }, label = { Text(text) })
                }
            }
        }
        if (nutritionFacts.hasMacronutrientsValues()) {
            NutrientsHeader(
                proteins = nutritionFacts.proteins.value,
                carbohydrates = nutritionFacts.carbohydrates.value,
                fats = nutritionFacts.fats.value,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                enabled = expandingEnabled,
                modifier = Modifier.fillMaxWidth().padding(contentPadding.horizontal()),
            )
            Spacer(Modifier.height(8.dp))
        }
        NutrientList(
            facts = nutritionFacts,
            expanded = expanded,
            modifier =
                Modifier.fillMaxWidth()
                    .padding(contentPadding.horizontal())
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = { onExpandedChange(!expanded) },
                    ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsEmptyNutrientsPreview() {
    PreviewFoodYouTheme {
        AddIngredientNutrients(
            nutritionFacts = NutritionFacts(),
            quantities = sampleQuantities,
            packageQuantity = AbsoluteQuantity.Weight(200.grams),
            servingQuantity = AbsoluteQuantity.Weight(30.grams),
            onSelectQuantity = {},
            expanded = false,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsPreview() {
    PreviewFoodYouTheme {
        AddIngredientNutrients(
            nutritionFacts = sampleNutritionFacts,
            quantities = sampleQuantities,
            packageQuantity = AbsoluteQuantity.Weight(200.grams),
            servingQuantity = AbsoluteQuantity.Weight(30.grams),
            onSelectQuantity = {},
            expanded = false,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsExpandedPreview() {
    PreviewFoodYouTheme {
        AddIngredientNutrients(
            nutritionFacts = sampleNutritionFacts,
            quantities = sampleQuantities,
            packageQuantity = AbsoluteQuantity.Weight(200.grams),
            servingQuantity = AbsoluteQuantity.Weight(30.grams),
            onSelectQuantity = {},
            expanded = true,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

private val sampleNutritionFacts =
    NutritionFacts(
        energy = NutrientValue.Complete(250.kilocalories),
        proteins = NutrientValue.Complete(15.grams),
        carbohydrates = NutrientValue.Complete(30.grams),
        fats = NutrientValue.Complete(10.grams),
        sugars = NutrientValue.Complete(5.grams),
        saturatedFats = NutrientValue.Complete(2.grams),
        dietaryFiber = NutrientValue.Complete(3.grams),
        salt = NutrientValue.Complete(0.5.grams),
    )

private val sampleQuantities =
    listOf(
        AbsoluteQuantity.Weight(100.grams),
        ServingQuantity(1.0),
        PackageQuantity(1.0),
        AbsoluteQuantity.Weight(100.grams),
    )
