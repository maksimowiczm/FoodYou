package com.maksimowiczm.foodyou.app.ui.food.details.userrecipe

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun RecipeIngredientListItem(
    component: FoodCompositionComponent,
    scalingFactor: Double,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val nameSelector = LocalFoodNameSelector.current
    val componentName =
        remember(component.name, nameSelector) { nameSelector.select(component.name) }

    val scaledComponentQuantity =
        remember(component.quantity, scalingFactor) {
            when (val q = component.quantity) {
                is FoodComponentComponentQuantity.Weight ->
                    q.copy(absoluteWeight = q.absoluteWeight * scalingFactor)

                is FoodComponentComponentQuantity.Package ->
                    q.copy(packages = q.packages * scalingFactor)

                is FoodComponentComponentQuantity.Serving ->
                    q.copy(servings = q.servings * scalingFactor)
            }
        }

    val componentMeasuredFacts =
        remember(component.measuredNutritionFacts, scalingFactor) {
            component.measuredNutritionFacts * scalingFactor
        }

    val shimmer = rememberShimmer(ShimmerBounds.View)

    val image: @Composable (() -> Unit)? = run {
        when (val image = component.image) {
            is FoodCompositionComponentImage.Blob -> {
                @Composable { resolveBlob(image.blob).Image(shimmer, Modifier.size(56.dp)) }
            }

            is FoodCompositionComponentImage.Uri -> {
                @Composable { image.uri.Image(shimmer, Modifier.size(56.dp)) }
            }

            null -> null
        }
    }

    FoodSearchListItem(
        headline = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(componentName)
                if (component.identity is FoodCompositionComponentIdentity.Recipe) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_skillet_filled),
                        contentDescription = null,
                    )
                }
            }
        },
        proteins = componentMeasuredFacts.proteins.value,
        carbohydrates = componentMeasuredFacts.carbohydrates.value,
        fats = componentMeasuredFacts.fats.value,
        energy = componentMeasuredFacts.energy.value,
        quantity = { Text(scaledComponentQuantity.stringResource()) },
        image = image,
        onClick = onClick,
        modifier = modifier,
    )
}
