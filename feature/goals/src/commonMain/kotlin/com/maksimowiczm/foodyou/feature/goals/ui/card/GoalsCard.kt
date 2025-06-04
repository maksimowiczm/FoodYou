package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.feature.goals.model.DailyGoals
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.negative_exceeded_by_calories
import foodyou.app.generated.resources.neutral_remaining_calories
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.positive_goal_reached
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GoalsCard(
    homeState: HomeState,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsCardViewModel = koinViewModel()
) {
    val diaryDay =
        viewModel.observeDiaryDay(homeState.selectedDate).collectAsStateWithLifecycle(null).value

    val expand = viewModel.expand.collectAsStateWithLifecycle().value

    if (diaryDay == null) {
        GoalsCardSkeleton(
            shimmer = homeState.shimmer,
            expand = expand,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    } else {
        GoalsCard(
            expand = expand,
            totalCalories = diaryDay.totalCalories,
            totalProteins = diaryDay.totalProteins,
            totalCarbohydrates = diaryDay.totalCarbohydrates,
            totalFats = diaryDay.totalFats,
            dailyGoals = diaryDay.dailyGoals,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun GoalsCard(
    expand: Boolean,
    totalCalories: Int,
    totalProteins: Int,
    totalCarbohydrates: Int,
    totalFats: Int,
    dailyGoals: DailyGoals,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proteinsPercentage = animateFloatAsState(
        targetValue = totalProteins / dailyGoals.proteinsAsGrams,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    ).value

    val carbsPercentage = animateFloatAsState(
        targetValue = totalCarbohydrates / dailyGoals.carbohydratesAsGrams,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    ).value

    val fatsPercentage = animateFloatAsState(
        targetValue = totalFats / dailyGoals.fatsAsGrams,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    ).value

    FoodYouHomeCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            GoalsCardContent(
                calories = totalCalories,
                caloriesGoal = dailyGoals.calories,
                proteinsPercentage = proteinsPercentage,
                carbsPercentage = carbsPercentage,
                fatsPercentage = fatsPercentage,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = expand,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    ExpandedCardContent(
                        proteinsGrams = totalProteins,
                        proteinsGoalGrams = dailyGoals.proteinsAsGrams.roundToInt(),
                        carbohydratesGrams = totalCarbohydrates,
                        carbohydratesGoalGrams = dailyGoals.carbohydratesAsGrams.roundToInt(),
                        fatsGrams = totalFats,
                        fatsGoalGrams = dailyGoals.fatsAsGrams.roundToInt(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsCardContent(
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

    val caloriesString = remember(calories, caloriesGoal, kcal, typography, colorScheme) {
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

    Row(
        modifier = modifier,
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
            val barHeight = 1 - overflowFraction

            drawRect(
                color = barColor,
                size = Size(
                    width = size.width,
                    height = size.height * barHeight - 1.dp.toPx()
                )
            )
            drawRect(
                color = overflowColor,
                topLeft = Offset(
                    x = 0f,
                    y = size.height * barHeight + 1.dp.toPx()
                ),
                size = Size(
                    width = size.width,
                    height = size.height * overflowFraction - 1.dp.toPx()
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

@Composable
private fun ExpandedCardContent(
    proteinsGrams: Int,
    proteinsGoalGrams: Int,
    carbohydratesGrams: Int,
    carbohydratesGoalGrams: Int,
    fatsGrams: Int,
    fatsGoalGrams: Int,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val nutrientsPalette = LocalNutrientsPalette.current
    val gramShort = stringResource(Res.string.unit_gram_short)

    val proteinsString = buildAnnotatedString {
        val color = if (proteinsGrams > proteinsGoalGrams) {
            colorScheme.error
        } else {
            nutrientsPalette.proteinsOnSurfaceContainer
        }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) {
            append(" $proteinsGrams ")
        }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $proteinsGoalGrams $gramShort")
        }
    }

    val carbohydratesString = buildAnnotatedString {
        val color = if (carbohydratesGrams > carbohydratesGoalGrams) {
            colorScheme.error
        } else {
            nutrientsPalette.carbohydratesOnSurfaceContainer
        }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) {
            append(" $carbohydratesGrams ")
        }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $carbohydratesGoalGrams $gramShort")
        }
    }

    val fatsString = buildAnnotatedString {
        val color = if (fatsGrams > fatsGoalGrams) {
            colorScheme.error
        } else {
            nutrientsPalette.fatsOnSurfaceContainer
        }

        withStyle(typography.headlineSmall.merge(color).toSpanStyle()) {
            append(" $fatsGrams ")
        }
        withStyle(typography.bodyMedium.merge(colorScheme.outline).toSpanStyle()) {
            append("/ $fatsGoalGrams $gramShort")
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoundedSquare(
                color = LocalNutrientsPalette.current.proteinsOnSurfaceContainer
            )

            Text(
                text = stringResource(Res.string.nutriment_proteins),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = proteinsString,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoundedSquare(
                color = LocalNutrientsPalette.current.carbohydratesOnSurfaceContainer
            )

            Text(
                text = stringResource(Res.string.nutriment_carbohydrates),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = carbohydratesString,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoundedSquare(
                color = LocalNutrientsPalette.current.fatsOnSurfaceContainer
            )

            Text(
                text = stringResource(Res.string.nutriment_fats),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = fatsString,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun RoundedSquare(color: Color, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(16.dp)
            .clip(MaterialTheme.shapes.extraSmall)
    ) {
        drawRect(color = color, size = size)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsCardSkeleton(
    shimmer: Shimmer,
    expand: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(
                        Modifier
                            .shimmer(shimmer)
                            .width(60.dp)
                            .height(MaterialTheme.typography.headlineLargeEmphasized.toDp())
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )

                    Spacer(
                        Modifier
                            .shimmer(shimmer)
                            .size(120.dp, MaterialTheme.typography.bodyMediumEmphasized.toDp())
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                }

                Row(
                    modifier = Modifier.height(64.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer)
                    )

                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer)
                    )

                    MacroBar(
                        progress = 1f,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        barColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.shimmer(shimmer)
                    )
                }
            }

            AnimatedVisibility(
                visible = expand,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer)
                        )

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer)
                        )

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundedSquare(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.shimmer(shimmer)
                        )

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .width(100.dp)
                                .height(MaterialTheme.typography.labelLarge.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )

                        Spacer(Modifier.weight(1f))

                        Spacer(
                            Modifier
                                .shimmer(shimmer)
                                .size(80.dp, MaterialTheme.typography.headlineSmall.toDp() - 4.dp)
                                .padding(vertical = 2.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }
                }
            }
        }
    }
}
