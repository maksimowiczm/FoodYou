package com.maksimowiczm.foodyou.feature.diary.addfood.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.core.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import kotlin.math.max

/**
 * Indicator with goal.
 */
@Composable
fun CaloriesProgressIndicator(
    calories: Float,
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    goal: Float,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val animatedProteins by animateFloatAsState(proteins)
    val animatedCarbohydrates by animateFloatAsState(carbohydrates)
    val animatedFats by animateFloatAsState(fats)

    val max = max(goal, calories)
    val animatedMax by animateFloatAsState(max.toFloat())

    MultiColorProgressIndicator(
        items = listOf(
            MultiColorProgressIndicatorItem(
                progress = animatedProteins / animatedMax,
                color = nutrientsPalette.proteinsOnSurfaceContainer
            ),
            MultiColorProgressIndicatorItem(
                progress = animatedCarbohydrates / animatedMax,
                color = nutrientsPalette.carbohydratesOnSurfaceContainer
            ),
            MultiColorProgressIndicatorItem(
                progress = animatedFats / animatedMax,
                color = nutrientsPalette.fatsOnSurfaceContainer
            )
        ),
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
fun CaloriesProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val sum = proteins + carbohydrates + fats

    MultiColorProgressIndicator(
        items = listOf(
            MultiColorProgressIndicatorItem(
                progress = proteins / sum,
                color = nutrientsPalette.proteinsOnSurfaceContainer
            ),
            MultiColorProgressIndicatorItem(
                progress = carbohydrates / sum,
                color = nutrientsPalette.carbohydratesOnSurfaceContainer
            ),
            MultiColorProgressIndicatorItem(
                progress = fats / sum,
                color = nutrientsPalette.fatsOnSurfaceContainer
            )
        ),
        modifier = modifier
            .defaultMinSize(minWidth = 0.dp, minHeight = 16.dp)
            .clip(MaterialTheme.shapes.small)
    )
}
