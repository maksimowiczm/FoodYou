package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.utility.LocalBlobResolver
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserRecipeListItem(
    recipe: SearchResult.UserRecipe,
    onClick: (Quantity) -> Unit,
    shimmer: Shimmer,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    modifier: Modifier = Modifier,
    preferredQuantity: Quantity = ServingQuantity(1.0),
) {
    val servingQuantity =
        remember(recipe.servingWeight) { AbsoluteQuantity.Weight(recipe.servingWeight) }
    val totalQuantity = remember(recipe.totalWeight) { AbsoluteQuantity.Weight(recipe.totalWeight) }

    val factor =
        remember(preferredQuantity, totalQuantity, servingQuantity) {
            when (preferredQuantity) {
                is AbsoluteQuantity.Volume -> preferredQuantity.volume.milliliters / 100.0
                is AbsoluteQuantity.Weight -> preferredQuantity.weight.grams / 100.0
                is PackageQuantity ->
                    (totalQuantity.weight.grams / 100.0) * preferredQuantity.packages

                is ServingQuantity ->
                    (servingQuantity.weight.grams / 100.0) * preferredQuantity.servings
            }
        }

    val measurementFacts =
        remember(recipe.nutritionFacts, factor) { recipe.nutritionFacts * factor }

    val measurementString =
        preferredQuantity
            .stringResource(totalQuantity, servingQuantity)
            .expect("PreferredQuantity string can't be null")

    FoodSearchListItem(
        headline = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(recipe.headline(LocalFoodNameSelector.current))
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.ic_skillet_filled),
                    contentDescription = null,
                )
            }
        },
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image =
            recipe.image?.let {
                @Composable {
                    LocalBlobResolver.current.resolve(it).Image(shimmer, Modifier.size(56.dp))
                }
            },
        onClick = { onClick(preferredQuantity) },
        interactionSource = interactionSource,
        shape = shape,
        modifier = modifier,
    )
}
