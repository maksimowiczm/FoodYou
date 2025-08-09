package com.maksimowiczm.foodyou.feature.food.shared.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.shared.ui.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.shared.ui.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import kotlin.math.max
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

/** Indicator with goal. */
@Composable
fun EnergyProgressIndicator(
    calories: Float,
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    goal: Float,
    order: List<NutrientsOrder>,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val animatedProteins by animateFloatAsState(proteins)
    val animatedCarbohydrates by animateFloatAsState(carbohydrates)
    val animatedFats by animateFloatAsState(fats)

    val max = max(goal, calories)
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
fun EnergyProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    order: List<NutrientsOrder>,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current

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

/**
 * Overloaded version of [EnergyProgressIndicator] that uses the current [NutrientsOrder] from
 * [ObserveSettingsUseCase].
 */
@Composable
fun EnergyProgressIndicator(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    modifier: Modifier = Modifier,
) {
    val observeSettingsUseCase: ObserveSettingsUseCase = koinInject()

    val order =
        observeSettingsUseCase
            .observe()
            .map { it.nutrientsOrder }
            .collectAsStateWithLifecycle(
                runBlocking { observeSettingsUseCase.observe().map { it.nutrientsOrder }.first() }
            )
            .value

    EnergyProgressIndicator(
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        order = order,
        modifier = modifier,
    )
}
