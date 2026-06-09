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
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.getOrNull
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
                    FoodComponentComponentQuantity.Weight(q.weight * scalingFactor)

                is FoodComponentComponentQuantity.Package ->
                    FoodComponentComponentQuantity.Package(
                        q.quantity * scalingFactor,
                        q.packageWeight,
                    )

                is FoodComponentComponentQuantity.Serving ->
                    FoodComponentComponentQuantity.Serving(
                        q.quantity * scalingFactor,
                        q.servingWeight,
                    )
            }
        }

    val componentMeasuredFacts =
        remember(component.measuredNutritionFacts, scalingFactor) {
            component.measuredNutritionFacts * scalingFactor
        }

    val componentPackageQuantity =
        remember(scaledComponentQuantity) {
            when (scaledComponentQuantity) {
                is FoodComponentComponentQuantity.Package ->
                    AbsoluteQuantity.Weight(scaledComponentQuantity.packageWeight)

                else -> null
            }
        }
    val componentServingQuantity =
        remember(scaledComponentQuantity) {
            when (scaledComponentQuantity) {
                is FoodComponentComponentQuantity.Serving ->
                    AbsoluteQuantity.Weight(scaledComponentQuantity.servingWeight)

                else -> null
            }
        }

    val quantityString =
        when (scaledComponentQuantity) {
            is FoodComponentComponentQuantity.Weight ->
                AbsoluteQuantity.Weight(scaledComponentQuantity.weight).stringResource()

            is FoodComponentComponentQuantity.Package ->
                PackageQuantity(scaledComponentQuantity.quantity)
                    .stringResource(componentPackageQuantity, componentServingQuantity)
                    .getOrNull()
                    ?: AbsoluteQuantity.Weight(scaledComponentQuantity.absoluteWeight)
                        .stringResource()

            is FoodComponentComponentQuantity.Serving ->
                ServingQuantity(scaledComponentQuantity.quantity)
                    .stringResource(componentPackageQuantity, componentServingQuantity)
                    .getOrNull()
                    ?: AbsoluteQuantity.Weight(scaledComponentQuantity.absoluteWeight)
                        .stringResource()
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
        quantity = { Text(quantityString) },
        image = image,
        onClick = onClick,
        modifier = modifier,
    )
}
