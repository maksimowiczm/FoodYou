package com.maksimowiczm.foodyou.core.ui.nutrition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.NutritionFactsField
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.MultiColorProgressIndicatorItem
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
    modifier: Modifier = Modifier,
    preference: NutritionFactsListPreference = userPreference()
) {
    val preferences by preference.collectAsStateWithLifecycle(preference.getBlocking())

    val nutrientsPalette = LocalNutrientsPalette.current

    val animatedProteins by animateFloatAsState(proteins)
    val animatedCarbohydrates by animateFloatAsState(carbohydrates)
    val animatedFats by animateFloatAsState(fats)

    val max = max(goal, calories)
    val animatedMax by animateFloatAsState(max.toFloat())

    val order = remember(preferences) {
        preferences.orderedEnabled.filter {
            it in listOf(
                NutritionFactsField.Proteins,
                NutritionFactsField.Carbohydrates,
                NutritionFactsField.Fats
            )
        }
    }

    MultiColorProgressIndicator(
        items = order.mapNotNull {
            when (it) {
                NutritionFactsField.Proteins -> MultiColorProgressIndicatorItem(
                    progress = animatedProteins / animatedMax,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )

                NutritionFactsField.Carbohydrates -> MultiColorProgressIndicatorItem(
                    progress = animatedCarbohydrates / animatedMax,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                NutritionFactsField.Fats -> MultiColorProgressIndicatorItem(
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
fun CaloriesProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    modifier: Modifier = Modifier,
    preference: NutritionFactsListPreference = userPreference()
) {
    val preferences by preference.collectAsStateWithLifecycle(preference.getBlocking())
    val nutrientsPalette = LocalNutrientsPalette.current

    val sum = proteins + carbohydrates + fats
    val items = remember(preferences, sum, proteins, carbohydrates, fats) {
        preferences.orderedEnabled.filter {
            it in listOf(
                NutritionFactsField.Proteins,
                NutritionFactsField.Carbohydrates,
                NutritionFactsField.Fats
            )
        }.mapNotNull {
            when (it) {
                NutritionFactsField.Proteins -> MultiColorProgressIndicatorItem(
                    progress = proteins / sum,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )

                NutritionFactsField.Carbohydrates -> MultiColorProgressIndicatorItem(
                    progress = carbohydrates / sum,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                NutritionFactsField.Fats -> MultiColorProgressIndicatorItem(
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
