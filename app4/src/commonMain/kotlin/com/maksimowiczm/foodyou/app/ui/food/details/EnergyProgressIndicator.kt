package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.app.ui.common.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import kotlin.math.max

/** Indicator with goal. */
@Composable
internal fun EnergyProgressIndicator(
    energy: Float,
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    goal: Float,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val order = LocalNutrientsOrder.current

    val animatedProteins by animateFloatAsState(proteins)
    val animatedCarbohydrates by animateFloatAsState(carbohydrates)
    val animatedFats by animateFloatAsState(fats)

    val max = max(goal, energy)
    val animatedMax by animateFloatAsState(max)

    MultiColorProgressIndicator(
        items =
            order.mapNotNull {
                when (it) {
                    NutrientsOrder.Proteins ->
                        MultiColorProgressIndicatorItem(
                            progress = animatedProteins / animatedMax,
                            color = nutrientsPalette.proteinsOnSurfaceContainer,
                        )

                    NutrientsOrder.Carbohydrates ->
                        MultiColorProgressIndicatorItem(
                            progress = animatedCarbohydrates / animatedMax,
                            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        )

                    NutrientsOrder.Fats ->
                        MultiColorProgressIndicatorItem(
                            progress = animatedFats / animatedMax,
                            color = nutrientsPalette.fatsOnSurfaceContainer,
                        )

                    else -> null
                }
            },
        modifier =
            modifier
                .defaultMinSize(minWidth = 0.dp, minHeight = 16.dp)
                .clip(MaterialTheme.shapes.small),
    )
}

/** Indicator without goal. */
@Composable
internal fun EnergyProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val order = LocalNutrientsOrder.current

    val sum = proteins + carbohydrates + fats
    val items =
        remember(order, sum, proteins, carbohydrates, fats) {
            order.mapNotNull {
                when (it) {
                    NutrientsOrder.Proteins ->
                        MultiColorProgressIndicatorItem(
                            progress = proteins / sum,
                            color = nutrientsPalette.proteinsOnSurfaceContainer,
                        )

                    NutrientsOrder.Carbohydrates ->
                        MultiColorProgressIndicatorItem(
                            progress = carbohydrates / sum,
                            color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        )

                    NutrientsOrder.Fats ->
                        MultiColorProgressIndicatorItem(
                            progress = fats / sum,
                            color = nutrientsPalette.fatsOnSurfaceContainer,
                        )

                    else -> null
                }
            }
        }

    MultiColorProgressIndicator(
        items = items,
        modifier =
            modifier
                .defaultMinSize(minWidth = 0.dp, minHeight = 16.dp)
                .clip(MaterialTheme.shapes.small),
    )
}
