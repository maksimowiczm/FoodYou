package com.maksimowiczm.foodyou.app.ui.food.common

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
import com.maksimowiczm.foodyou.app.ui.common.InteractionShapes
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

interface FoodUiScopeWithIngredients {
    val components: List<FoodCompositionComponent>
    val ingredientScalingFactor: Double
}

@Composable
fun FoodUiScopeWithIngredients.Ingredients(
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (components.isEmpty()) return

    val hasNestedRecipe =
        remember(components) {
            components.any { it.identity is FoodCompositionComponentIdentity.Composite }
        }

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
internal fun RecipeIngredientListItem(
    component: FoodCompositionComponent,
    scalingFactor: Double,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
    unwrap: Boolean = true,
    isLast: Boolean = false,
) {
    val shapes = MaterialTheme.shapes
    val interactionSource = remember { MutableInteractionSource() }
    val baseShape =
        remember(shapes, component) {
            if (component is FoodCompositionComponent.Simple && unwrap) shapes.large
            else if (component is FoodCompositionComponent.Composite && unwrap)
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
        if (unwrap && component is FoodCompositionComponent.Composite) {
            val subScalingFactor =
                remember(component, scalingFactor) {
                    val totalWeight = component.components.totalWeight
                    if (totalWeight.grams > 0) {
                        scalingFactor *
                            (component.quantity.absoluteWeight.grams / totalWeight.grams)
                    } else {
                        scalingFactor
                    }
                }

            component.components.forEachIndexed { i, subComponent ->
                Spacer(Modifier.height(2.dp))
                RecipeIngredientListItem(
                    component = subComponent,
                    scalingFactor = subScalingFactor,
                    onNavigateToIngredient = onNavigateToIngredient,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                    unwrap = false,
                    isLast = i == component.components.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun RecipeIngredientListItemContent(
    component: FoodCompositionComponent,
    scalingFactor: Double,
    shape: Shape,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit = { _, _ -> },
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

    Surface(
        onClick = {
            onNavigateToIngredient(component.identity, scaledComponentQuantity.toQuantity())
        },
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        interactionSource = interactionSource,
    ) {
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
            onClick = null,
            modifier = Modifier,
        )
    }
}
