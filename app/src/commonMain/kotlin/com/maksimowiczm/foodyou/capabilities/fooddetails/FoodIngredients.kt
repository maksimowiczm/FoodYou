package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.component.FoodListItem
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun FoodIngredients(
    components: List<MeasuredFoodSnapshot>,
    ingredientScalingFactor: Double,
    onNavigateToIngredient: ((FoodSnapshotId, Quantity) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (components.isEmpty()) return

    val hasNestedRecipe =
        remember(components) { components.any { it.snapshot is CompositeFoodSnapshot } }

    Column(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        verticalArrangement =
            if (hasNestedRecipe) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(2.dp),
    ) {
        components.forEach { component ->
            RecipeIngredientListItem(
                component = component,
                scalingFactor = ingredientScalingFactor,
                onNavigateToIngredient = onNavigateToIngredient,
                modifier = Modifier.fillMaxWidth(),
                unwrap = hasNestedRecipe,
            )
        }
    }
}

@Composable
private fun RecipeIngredientListItem(
    component: MeasuredFoodSnapshot,
    scalingFactor: Double,
    onNavigateToIngredient: ((FoodSnapshotId, Quantity) -> Unit)?,
    modifier: Modifier = Modifier,
    unwrap: Boolean = true,
    isLast: Boolean = false,
) {
    val shapes = MaterialTheme.shapes
    val interactionSource = remember { MutableInteractionSource() }
    val baseShape =
        remember(shapes, component) {
            if (component.snapshot is LeafFoodSnapshot && unwrap) shapes.large
            else if (component.snapshot is CompositeFoodSnapshot && unwrap)
                shapes.large.copy(bottomEnd = shapes.extraSmall.bottomEnd)
            else if (isLast)
                shapes.extraSmall.copy(
                    bottomEnd = shapes.large.bottomEnd,
                    bottomStart = shapes.large.bottomStart,
                )
            else shapes.extraSmall
        }
    val shape =
        rememberInteractionAnimatedShape(
            InteractionShapes(
                shape = baseShape,
                pressedShape = MaterialTheme.shapes.large,
            ),
            interactionSource,
        )

    Column(modifier) {
        RecipeIngredientListItemContent(
            component = component,
            scalingFactor = scalingFactor,
            onNavigateToIngredient = onNavigateToIngredient,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            interactionSource = interactionSource,
        )
        val snapshot = component.snapshot
        if (unwrap && snapshot is CompositeFoodSnapshot) {
            val subScalingFactor =
                remember(component, scalingFactor) {
                    val totalWeight = snapshot.components.totalWeight
                    if (totalWeight.grams > 0) {
                        scalingFactor *
                            (component.quantity.absoluteWeight.grams / totalWeight.grams)
                    } else {
                        scalingFactor
                    }
                }

            snapshot.components.forEachIndexed { i, subComponent ->
                Spacer(Modifier.height(2.dp))
                RecipeIngredientListItem(
                    component = subComponent,
                    scalingFactor = subScalingFactor,
                    onNavigateToIngredient = onNavigateToIngredient,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                    unwrap = false,
                    isLast = i == snapshot.components.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun RecipeIngredientListItemContent(
    component: MeasuredFoodSnapshot,
    scalingFactor: Double,
    shape: Shape,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    onNavigateToIngredient: ((FoodSnapshotId, Quantity) -> Unit)? = null,
) {
    val nameSelector = LocalFoodNameSelector.current
    val componentName =
        remember(component.name, nameSelector) { nameSelector.select(component.name) }

    val scaledComponentQuantity =
        remember(component.quantity, scalingFactor) {
            when (val q = component.quantity) {
                is FoodSnapshotQuantity.Weight ->
                    q.copy(absoluteWeight = q.absoluteWeight * scalingFactor)

                is FoodSnapshotQuantity.Package -> q.copy(packages = q.packages * scalingFactor)

                is FoodSnapshotQuantity.Serving -> q.copy(servings = q.servings * scalingFactor)
            }
        }

    val componentMeasuredFacts =
        remember(component.measuredNutritionFacts, scalingFactor) {
            component.measuredNutritionFacts * scalingFactor
        }

    val shimmer = rememberShimmer(ShimmerBounds.View)

    val image: @Composable (() -> Unit)? = run {
        when (val image = component.image) {
            is FoodSnapshotImage.Blob -> {
                @Composable { resolveBlob(image.blob).Image(shimmer, Modifier.size(56.dp)) }
            }

            is FoodSnapshotImage.Uri -> {
                @Composable { image.uri.Image(shimmer, Modifier.size(56.dp)) }
            }

            null -> null
        }
    }

    val content =
        @Composable {
            FoodListItem(
                headline = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(componentName)
                        if (component.id is FoodSnapshotId.UserRecipe) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(Res.drawable.ic_skillet_filled),
                                contentDescription = null,
                            )
                        }
                    }
                },
                proteins = { Text(componentMeasuredFacts.proteins.value?.stringResource() ?: "?") },
                carbohydrates = {
                    Text(componentMeasuredFacts.carbohydrates.value?.stringResource() ?: "?")
                },
                fats = { Text(componentMeasuredFacts.fats.value?.stringResource() ?: "?") },
                energy = {
                    Text(
                        componentMeasuredFacts.energy.value
                            ?.inUnit(LocalEnergyUnit.current)
                            ?.stringResource() ?: "?"
                    )
                },
                quantity = { Text(scaledComponentQuantity.stringResource()) },
                image = image,
                modifier = Modifier,
            )
        }

    if (onNavigateToIngredient != null)
        Surface(
            onClick = {
                onNavigateToIngredient(component.id, scaledComponentQuantity.toQuantity())
            },
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = shape,
            interactionSource = interactionSource,
            content = content,
        )
    else
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = shape,
            content = content,
        )
}
