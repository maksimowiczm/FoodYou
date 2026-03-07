package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchListItem(
    headline: String,
    proteins: Double?,
    carbohydrates: Double?,
    fats: Double?,
    energy: Double?,
    quantity: @Composable () -> Unit,
    image: @Composable (() -> Unit)?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    FoodListItem(
        headline = { Text(text = headline) },
        image = image,
        proteins = {
            val text = proteins?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        carbohydrates = {
            val text = carbohydrates?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        fats = {
            val text = fats?.formatClipZeros() ?: "?"
            Text("$text $g")
        },
        energy = {
            val text = LocalEnergyFormatter.current.formatEnergy(energy?.roundToInt())
            Text(text)
        },
        quantity = quantity,
        modifier = modifier,
        onClick = onClick,
    )
}
