package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource

@Composable
fun QuantitySuggestions(
    suggestions: List<Quantity>,
    selectedType: QuantityType,
    formField: FormField,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    onSelectQuantity: (Quantity) -> Unit,
    modifier: Modifier = Modifier.Companion,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
) {
    val stringedQuantities = suggestions.mapNotNull { quantity ->
        quantity.stringResource(packageQuantity, servingQuantity).getOrNull()?.let {
            quantity to it
        }
    }
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        items(stringedQuantities) { (quantity, text) ->
            val isSelected =
                remember(quantity, selectedType, formField.textFieldState.text) {
                    val currentAmount = formField.textFieldState.text.toString().toDoubleOrNull()
                    currentAmount?.let { selectedType.toQuantity(it) } == quantity
                }
            FilterChip(
                selected = isSelected,
                onClick = { onSelectQuantity(quantity) },
                label = { Text(text) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantitySuggestionsPreview() {
    PreviewFoodYouTheme {
        QuantitySuggestions(
            suggestions =
                listOf(
                    AbsoluteQuantity.Weight(100.grams),
                    AbsoluteQuantity.Weight(200.grams),
                ),
            selectedType = QuantityType.Gram,
            formField = FormField(),
            packageQuantity = AbsoluteQuantity.Weight(200.grams),
            servingQuantity = AbsoluteQuantity.Weight(30.grams),
            onSelectQuantity = {},
        )
    }
}
