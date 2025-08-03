package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals

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
import androidx.compose.runtime.*
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
import com.maksimowiczm.foodyou.core.ext.sumOf
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.HomeState
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.ExpandGoalsCard
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GoalsCard(
    homeState: HomeState,
    onClick: (epochDay: Long) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = koinViewModel(),
    expandGoalsCardPreference: ExpandGoalsCard = userPreference()
) {
    val date = homeState.selectedDate
    val meals = viewModel.observeMeals(date).collectAsStateWithLifecycle().value
    val goals = viewModel.observeGoals(date).collectAsStateWithLifecycle().value
    val expand by expandGoalsCardPreference.collectAsStateWithLifecycleInitialBlock()

    if (meals == null || goals == null) {
        GoalsCardSkeleton(
            shimmer = homeState.shimmer,
            expand = expand,
            onClick = { onClick(date.toEpochDays()) },
            onLongClick = onLongClick,
            modifier = modifier
        )
    } else {
        val energy = meals.sumOf { it.energy }.roundToInt()
        val proteins = meals.sumOf { it.proteins }.roundToInt()
        val carbohydrates = meals.sumOf { it.carbohydrates }.roundToInt()
        val fats = meals.sumOf { it.fats }.roundToInt()

        GoalsCard(
            expand = expand,
            totalCalories = energy,
            totalProteins = proteins,
            totalCarbohydrates = carbohydrates,
            totalFats = fats,
            dailyGoal = goals,
            onClick = { onClick(date.toEpochDays()) },
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
    dailyGoal: DailyGoal,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proteinsTarget = dailyGoal[NutritionFactsField.Proteins].toFloat()
    val carbsTarget = dailyGoal[NutritionFactsField.Carbohydrates].toFloat()
    val fatsTarget = dailyGoal[NutritionFactsField.Fats].toFloat()

    val proteinsPercentage = animateFloatAsState(
        targetValue = totalProteins / proteinsTarget,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    ).value

    val carbsPercentage = animateFloatAsState(
        targetValue = totalCarbohydrates / carbsTarget,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    ).value

    val fatsPercentage = animateFloatAsState(
        targetValue = totalFats / fatsTarget,
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
                caloriesGoal = dailyGoal[NutritionFactsField.Energy].roundToInt(),
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
                        proteinsGoalGrams = proteinsTarget.roundToInt(),
                        carbohydratesGrams = totalCarbohydrates,
                        carbohydratesGoalGrams = carbsTarget.roundToInt(),
                        fatsGrams = totalFats,
                        fatsGoalGrams = fatsTarget.roundToInt(),
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
    modifier: Modifier = Modifier,
    preference: NutrientsOrderPreference = userPreference()
) {
    val preferences by preference.collectAsStateWithLifecycleInitialBlock()

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
            preferences.forEach { field ->
                when (field) {
                    NutrientsOrder.Proteins -> MacroBar(
                        progress = proteinsPercentage,
                        containerColor = nutrientsPalette.proteinsOnSurfaceContainer.copy(
                            alpha = .25f
                        ),
                        barColor = nutrientsPalette.proteinsOnSurfaceContainer
                    )

                    NutrientsOrder.Fats -> MacroBar(
                        progress = fatsPercentage,
                        containerColor = nutrientsPalette.fatsOnSurfaceContainer.copy(
                            alpha = .25f
                        ),
                        barColor = nutrientsPalette.fatsOnSurfaceContainer
                    )

                    NutrientsOrder.Carbohydrates -> MacroBar(
                        progress = carbsPercentage,
                        containerColor = nutrientsPalette.carbohydratesOnSurfaceContainer.copy(
                            alpha = .25f
                        ),
                        barColor = nutrientsPalette.carbohydratesOnSurfaceContainer
                    )

                    NutrientsOrder.Other,
                    NutrientsOrder.Vitamins,
                    NutrientsOrder.Minerals -> Unit
                }
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
    modifier: Modifier = Modifier,
    preference: NutrientsOrderPreference = userPreference()
) {
    val preferences by preference.collectAsStateWithLifecycleInitialBlock()

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
        preferences.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (it) {
                    NutrientsOrder.Proteins -> {
                        RoundedSquare(LocalNutrientsPalette.current.proteinsOnSurfaceContainer)

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

                    NutrientsOrder.Carbohydrates -> {
                        RoundedSquare(LocalNutrientsPalette.current.carbohydratesOnSurfaceContainer)

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

                    NutrientsOrder.Fats -> {
                        RoundedSquare(LocalNutrientsPalette.current.fatsOnSurfaceContainer)

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

                    NutrientsOrder.Other,
                    NutrientsOrder.Vitamins,
                    NutrientsOrder.Minerals -> Unit
                }
            }
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
