package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.shared.ui.component.FoodListItem
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource

@Composable
fun FoodSearchListItem(
    headline: String,
    proteins: Weight?,
    carbohydrates: Weight?,
    fats: Weight?,
    energy: Energy?,
    quantity: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    FoodSearchListItem(
        headline = { Text(text = headline) },
        image = image,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy,
        quantity = quantity,
        modifier = modifier,
        onClick = onClick,
        interactionSource = interactionSource,
        shape = shape,
    )
}

@Composable
fun FoodSearchListItem(
    headline: @Composable () -> Unit,
    proteins: Weight?,
    carbohydrates: Weight?,
    fats: Weight?,
    energy: Energy?,
    quantity: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    FoodListItem(
        headline = headline,
        image = image,
        proteins = { Text(proteins?.stringResource() ?: "?") },
        carbohydrates = { Text(carbohydrates?.stringResource() ?: "?") },
        fats = { Text(fats?.stringResource() ?: "?") },
        energy = { Text(energy?.inUnit(LocalEnergyUnit.current)?.stringResource() ?: "?") },
        quantity = quantity,
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
    )
}
