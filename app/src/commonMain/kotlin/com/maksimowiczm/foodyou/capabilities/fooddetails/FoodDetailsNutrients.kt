package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.extension.vertical
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource

@Composable
fun FoodDetailsNutrientsWithSuggestions(
    scaledNutritionFacts: NutritionFacts?,
    suggestions: List<Quantity>,
    selectedQuantity: Quantity?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    onSelectQuantity: (Quantity) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandingEnabled: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
) {
    val nutritionFacts = scaledNutritionFacts ?: return

    val stringifiedQuantities = suggestions.mapNotNull { quantity ->
        quantity.stringResource(packageQuantity, servingQuantity).getOrNull()?.let {
            quantity to it
        }
    }

    Column(modifier.padding(contentPadding.vertical())) {
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
        }
        if (stringifiedQuantities.isNotEmpty()) {
            LazyRow(
                contentPadding = contentPadding.horizontal(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(stringifiedQuantities) { (quantity, text) ->
                    FilterChip(
                        selected = quantity == selectedQuantity,
                        onClick = { onSelectQuantity(quantity) },
                        label = { Text(text) },
                    )
                }
            }
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
                        enabled = expandingEnabled,
                    ),
        )
    }
}

@Composable
fun FoodDetailsNutrientsCompact(
    scaledNutritionFacts: NutritionFacts?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandingEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val nutritionFacts = scaledNutritionFacts ?: return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (nutritionFacts.hasMacronutrientsValues()) {
            NutrientsHeader(
                proteins = nutritionFacts.proteins.value,
                carbohydrates = nutritionFacts.carbohydrates.value,
                fats = nutritionFacts.fats.value,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                enabled = expandingEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        NutrientList(
            facts = nutritionFacts,
            expanded = expanded,
            modifier =
                Modifier.fillMaxWidth()
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = { onExpandedChange(!expanded) },
                        enabled = expandingEnabled,
                    ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsWithSuggestionsPreview() {
    PreviewFoodYouTheme {
        FoodDetailsNutrientsWithSuggestions(
            scaledNutritionFacts = PreviewNutritionFacts,
            suggestions = PreviewSuggestions,
            selectedQuantity = PreviewSuggestions.first(),
            packageQuantity = PreviewPackageQuantity,
            servingQuantity = PreviewServingQuantity,
            onSelectQuantity = {},
            expanded = false,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsWithSuggestionsExpandedPreview() {
    PreviewFoodYouTheme {
        FoodDetailsNutrientsWithSuggestions(
            scaledNutritionFacts = PreviewNutritionFacts,
            suggestions = PreviewSuggestions,
            selectedQuantity = PreviewSuggestions.first(),
            packageQuantity = PreviewPackageQuantity,
            servingQuantity = PreviewServingQuantity,
            onSelectQuantity = {},
            expanded = true,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsCompactPreview() {
    PreviewFoodYouTheme {
        FoodDetailsNutrientsCompact(
            scaledNutritionFacts = PreviewNutritionFacts,
            expanded = false,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodDetailsNutrientsCompactExpandedPreview() {
    PreviewFoodYouTheme {
        FoodDetailsNutrientsCompact(
            scaledNutritionFacts = PreviewNutritionFacts,
            expanded = true,
            onExpandedChange = {},
            expandingEnabled = true,
        )
    }
}

private val PreviewSuggestions =
    listOf(
        AbsoluteQuantity.Weight(100.grams),
        AbsoluteQuantity.Weight(200.grams),
    )
private val PreviewNutritionFacts =
    NutritionFacts(
        energy = NutrientValue.Complete(250.kilocalories),
        proteins = NutrientValue.Complete(15.grams),
        carbohydrates = NutrientValue.Complete(30.grams),
        fats = NutrientValue.Complete(10.grams),
        sugars = NutrientValue.Complete(5.grams),
        saturatedFats = NutrientValue.Complete(2.grams),
        solubleFiber = NutrientValue.Complete(3.grams),
        salt = NutrientValue.Complete(0.5.grams),
    )
private val PreviewPackageQuantity = AbsoluteQuantity.Weight(200.grams)
private val PreviewServingQuantity = AbsoluteQuantity.Weight(30.grams)
