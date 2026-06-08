package com.maksimowiczm.foodyou.app.ui.food.details.userrecipe

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.getOrNull

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

    FoodSearchListItem(
        headline = componentName,
        proteins = componentMeasuredFacts.proteins.value,
        carbohydrates = componentMeasuredFacts.carbohydrates.value,
        fats = componentMeasuredFacts.fats.value,
        energy = componentMeasuredFacts.energy.value,
        quantity = { Text(quantityString) },
        image = null,
        onClick = onClick,
        modifier = modifier,
    )
}
