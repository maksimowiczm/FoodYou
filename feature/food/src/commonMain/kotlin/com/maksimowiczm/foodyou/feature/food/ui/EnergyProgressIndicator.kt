package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.core.ui.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import kotlin.math.max

/**
 * Indicator with goal.
 */
@Composable
fun EnergyProgressIndicator(
    calories: Float,
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    goal: Float,
    modifier: Modifier = Modifier,
    preference: NutrientsOrderPreference = userPreference()
) {
    val order = preference
        .collectAsStateWithLifecycle(preference.getBlocking())
        .value

    val nutrientsPalette = LocalNutrientsPalette.current

    val animatedProteins by animateFloatAsState(proteins)
    val animatedCarbohydrates by animateFloatAsState(carbohydrates)
    val animatedFats by animateFloatAsState(fats)

    val max = max(goal, calories)
    val animatedMax by animateFloatAsState(max.toFloat())

    MultiColorProgressIndicator(
        items = order.mapNotNull {
            when (it) {
                NutrientsOrder.Proteins -> MultiColorProgressIndicatorItem(
                    progress = animatedProteins / animatedMax,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )

                NutrientsOrder.Carbohydrates -> MultiColorProgressIndicatorItem(
                    progress = animatedCarbohydrates / animatedMax,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                NutrientsOrder.Fats -> MultiColorProgressIndicatorItem(
                    progress = animatedFats / animatedMax,
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )

                else -> null
            }
        },
        modifier = modifier
            .defaultMinSize(minWidth = 0.dp, minHeight = 16.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

/**
 * Indicator without goal.
 */
// Somehow padding reduces the height of the indicator. Not sure why.
@Composable
fun EnergyProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    modifier: Modifier = Modifier,
    preference: NutrientsOrderPreference = userPreference()
) {
    val order = preference
        .collectAsStateWithLifecycle(preference.getBlocking())
        .value

    val nutrientsPalette = LocalNutrientsPalette.current

    val sum = proteins + carbohydrates + fats
    val items = remember(order, sum, proteins, carbohydrates, fats) {
        order.mapNotNull {
            when (it) {
                NutrientsOrder.Proteins -> MultiColorProgressIndicatorItem(
                    progress = proteins / sum,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )

                NutrientsOrder.Carbohydrates -> MultiColorProgressIndicatorItem(
                    progress = carbohydrates / sum,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                NutrientsOrder.Fats -> MultiColorProgressIndicatorItem(
                    progress = fats / sum,
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )

                else -> null
            }
        }
    }

    MultiColorProgressIndicator(
        items = items,
        modifier = modifier
            .defaultMinSize(minWidth = 0.dp, minHeight = 16.dp)
            .clip(MaterialTheme.shapes.small)
    )
}
