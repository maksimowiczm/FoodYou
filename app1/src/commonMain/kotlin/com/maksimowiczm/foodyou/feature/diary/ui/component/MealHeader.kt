package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCardDefaults
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealHeader(
    headline: @Composable () -> Unit,
    time: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    spacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
    nutrientsLayout: @Composable () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        headline()
        time()
        spacer()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            nutrientsLayout()
        }
    }
}

@Composable
fun MealHeader(
    headline: @Composable () -> Unit,
    time: @Composable () -> Unit,
    caloriesLabel: @Composable () -> Unit,
    proteinsLabel: @Composable () -> Unit,
    carbohydratesLabel: @Composable () -> Unit,
    fatsLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    spacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
    nutrientsLayout: @Composable () -> Unit = {
        NutrientsLayout(
            caloriesLabel = caloriesLabel,
            proteinsLabel = proteinsLabel,
            carbohydratesLabel = carbohydratesLabel,
            fatsLabel = fatsLabel
        )
    }
) {
    MealHeader(
        headline = headline,
        time = time,
        modifier = modifier,
        spacer = spacer,
        nutrientsLayout = nutrientsLayout
    )
}

@Composable
fun NutrientsLayout(
    caloriesLabel: @Composable () -> Unit,
    proteinsLabel: @Composable () -> Unit,
    carbohydratesLabel: @Composable () -> Unit,
    fatsLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium
    ) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.unit_kcal)
                )
                caloriesLabel()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.proteinsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_proteins_short)
                    )
                    proteinsLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.carbohydratesOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_carbohydrates_short)
                    )
                    carbohydratesLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.fatsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_fats_short)
                    )
                    fatsLabel()
                }
            }
        }
    }
}

object MealHeaderTransitionKeys {
    data class MealContainer(val mealId: Long, val epochDay: Int)

    data class MealTitle(val mealId: Long, val epochDay: Int)

    data class MealTime(val mealId: Long, val epochDay: Int)

    data class MealNutrients(val mealId: Long, val epochDay: Int)
}

object MealHeaderTransitionSpecs {
    val containerColor: Color
        @Composable get() = FoodYouHomeCardDefaults.colors().containerColor

    val containerEnterTransition: EnterTransition = EnterTransition.None
    val containerExitTransition: ExitTransition = ExitTransition.None

    private val cardCornerRadius: Dp
        @Composable get() = 12.dp

    @Composable
    fun AnimatedVisibilityScope.overlayClipFromScreenToCard(): Shape {
        val roundedCornerAnimation by transition.animateDp {
            when (it) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> cardCornerRadius
            }
        }

        return RoundedCornerShape(roundedCornerAnimation)
    }

    @Composable
    fun AnimatedVisibilityScope.overlayClipFromCardToScreen(): Shape {
        val roundedCornerAnimation by transition.animateDp {
            when (it) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> cardCornerRadius
                EnterExitState.PostExit -> 0.dp
            }
        }

        return RoundedCornerShape(roundedCornerAnimation)
    }
}
