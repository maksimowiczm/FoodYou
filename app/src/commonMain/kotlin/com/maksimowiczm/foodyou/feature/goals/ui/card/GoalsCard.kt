package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun GoalsCard(
    homeState: HomeState,
    modifier: Modifier = Modifier,
    viewModel: GoalsCardViewModel = koinViewModel()
) {
    val diaryDay =
        viewModel.observeDiaryDay(homeState.selectedDate).collectAsStateWithLifecycle(null).value

    if (diaryDay != null) {
        val proteinsPercentage = animateFloatAsState(
            targetValue = diaryDay.totalProteins / diaryDay.dailyGoals.proteinsAsGrams,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
        ).value

        val carbsPercentage = animateFloatAsState(
            targetValue = diaryDay.totalCarbohydrates / diaryDay.dailyGoals.carbohydratesAsGrams,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
        ).value

        val fatsPercentage = animateFloatAsState(
            targetValue = diaryDay.totalFats / diaryDay.dailyGoals.fatsAsGrams,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
        ).value

        GoalsCard(
            calories = diaryDay.totalCalories,
            caloriesGoal = diaryDay.dailyGoals.calories,
            proteinsPercentage = proteinsPercentage,
            carbsPercentage = carbsPercentage,
            fatsPercentage = fatsPercentage,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsCard(
    calories: Int,
    caloriesGoal: Int,
    proteinsPercentage: Float,
    carbsPercentage: Float,
    fatsPercentage: Float,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val kcal = stringResource(Res.string.unit_kcal)
    val outlineColor = MaterialTheme.colorScheme.outline

    val caloriesString = remember(calories, caloriesGoal, kcal, typography) {
        buildAnnotatedString {
            withStyle(
                typography.headlineLargeEmphasized.merge(
                    color = when {
                        calories < caloriesGoal -> colorScheme.onSurface
                        calories == caloriesGoal -> colorScheme.onSurface
                        else -> colorScheme.error
                    }
                ).toSpanStyle()
            ) {
                append(calories.toString())
                append(" ")
            }
            withStyle(typography.bodyMedium.merge(outlineColor).toSpanStyle()) {
                append("/ $caloriesGoal $kcal")
            }
        }
    }

    val left = remember(calories, caloriesGoal) {
        caloriesGoal - calories
    }

    FoodYouHomeCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = caloriesString,
                    style = typography.headlineLargeEmphasized
                )

                when {
                    left > 0 -> Text(
                        text = pluralStringResource(
                            Res.plurals.neutral_remaining_calories,
                            left,
                            left
                        ),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )

                    left == 0 -> Text(
                        text = stringResource(Res.string.positive_goal_reached),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )

                    else -> Text(
                        text = pluralStringResource(
                            Res.plurals.negative_exceeded_by_calories,
                            -left,
                            -left
                        ),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )
                }
            }

            Row(
                modifier = Modifier.height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroBar(
                    progress = proteinsPercentage,
                    containerColor = nutrientsPalette.proteinsOnSurfaceContainer.copy(
                        alpha = .25f
                    ),
                    barColor = nutrientsPalette.proteinsOnSurfaceContainer
                )
                MacroBar(
                    progress = carbsPercentage,
                    containerColor = nutrientsPalette.carbohydratesOnSurfaceContainer.copy(
                        alpha = .25f
                    ),
                    barColor = nutrientsPalette.carbohydratesOnSurfaceContainer
                )
                MacroBar(
                    progress = fatsPercentage,
                    containerColor = nutrientsPalette.fatsOnSurfaceContainer.copy(
                        alpha = .25f
                    ),
                    barColor = nutrientsPalette.fatsOnSurfaceContainer
                )
            }
        }
    }
}

@Composable
private fun MacroBar(
    progress: Float,
    containerColor: Color,
    barColor: Color,
    modifier: Modifier = Modifier,
    overflowColor: Color = MaterialTheme.colorScheme.error
) {
    val containerFraction = (1 - progress).coerceIn(0f, 1f)
    val overflowFraction = (progress - 1).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )
            )
            .fillMaxHeight()
            .width(24.dp)
    ) {
        if (overflowFraction > 0f) {
            val remainingProgress = progress - overflowFraction

            drawRect(
                color = barColor,
                size = Size(
                    width = size.width,
                    height = size.height * overflowFraction - 1.dp.toPx()
                )
            )
            drawRect(
                color = overflowColor,
                topLeft = Offset(
                    x = 0f,
                    y = size.height * overflowFraction + 1.dp.toPx()
                ),
                size = Size(
                    width = size.width,
                    height = size.height * remainingProgress - 1.dp.toPx()
                )
            )
        } else {
            drawRect(
                color = containerColor,
                size = Size(
                    width = size.width,
                    height = size.height * containerFraction - 1.dp.toPx()
                )
            )
            drawRect(
                color = barColor,
                topLeft = Offset(
                    x = 0f,
                    y = size.height * containerFraction + 1.dp.toPx()
                ),
                size = Size(
                    width = size.width,
                    height = size.height * progress - 1.dp.toPx()
                )
            )
        }
    }
}
