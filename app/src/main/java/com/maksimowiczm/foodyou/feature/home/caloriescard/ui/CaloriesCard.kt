package com.maksimowiczm.foodyou.feature.home.caloriescard.ui

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.home.HomeState
import com.maksimowiczm.foodyou.feature.home.caloriescard.ui.ValueStatus.Achieved
import com.maksimowiczm.foodyou.feature.home.caloriescard.ui.ValueStatus.Exceeded
import com.maksimowiczm.foodyou.feature.home.caloriescard.ui.ValueStatus.Remaining
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.ui.preview.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlin.math.abs
import kotlin.math.max
import org.koin.androidx.compose.koinViewModel

@Composable
fun CaloriesCard(
    homeState: HomeState,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = koinViewModel()
) {
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val dd = diaryDay

    Crossfade(
        targetState = dd != null,
        modifier = modifier
    ) {
        if (it && dd != null) {
            CaloriesCard(
                diaryDay = dd
            )
        } else {
            CaloriesCardSkeleton(
                shimmerInstance = homeState.shimmer
            )
        }
    }
}

@Composable
private fun CaloriesCard(diaryDay: DiaryDay, modifier: Modifier = Modifier) {
    val calories = diaryDay.totalCalories
    val goal = diaryDay.dailyGoals.calories
    val valueStatus by remember(calories, goal) {
        derivedStateOf { calories withGoal goal }
    }
    val left by remember(calories, goal) {
        derivedStateOf { abs(goal - calories) }
    }

    val nutrientsPalette = LocalNutrientsPalette.current
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val kcalSuffix = stringResource(R.string.unit_kcal)

    val caloriesString =
        remember(valueStatus, left, nutrientsPalette, typography, colorScheme, kcalSuffix) {
            buildAnnotatedString {
                withStyle(
                    typography.headlineLarge.copy(
                        color = when (valueStatus) {
                            ValueStatus.Exceeded -> colorScheme.error
                            ValueStatus.Remaining,
                            ValueStatus.Achieved -> colorScheme.onSurface
                        }
                    ).toSpanStyle()
                ) {
                    append(calories.toString())
                }

                withStyle(
                    typography.bodyLarge.copy(
                        color = colorScheme.outline
                    ).toSpanStyle()
                ) {
                    append(" / $goal $kcalSuffix")
                }
            }
        }

    val animatedProteins by animateFloatAsState(diaryDay.totalCaloriesProteins.toFloat())
    val animatedCarbohydrates by animateFloatAsState(diaryDay.totalCaloriesCarbohydrates.toFloat())
    val animatedFats by animateFloatAsState(diaryDay.totalCaloriesFats.toFloat())

    FoodYouHomeCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.unit_calories),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = caloriesString,
                // Must manually set because otherwise its not same height
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(8.dp))

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
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(Modifier.height(8.dp))

            when (valueStatus) {
                ValueStatus.Exceeded -> Text(
                    text = pluralStringResource(
                        R.plurals.negative_exceeded_by_calories,
                        left,
                        left
                    ),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )

                ValueStatus.Remaining -> Text(
                    text = pluralStringResource(
                        R.plurals.neutral_remaining_calories,
                        left,
                        left
                    ),
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )

                ValueStatus.Achieved -> Text(
                    text = stringResource(R.string.positive_goal_reached),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NutrimentIndicator(
                    title = stringResource(R.string.nutriment_proteins),
                    value = diaryDay.totalProteins,
                    goal = diaryDay.dailyGoals.proteinsAsGrams,
                    progressColor = nutrientsPalette.proteinsOnSurfaceContainer
                )

                NutrimentIndicator(
                    title = stringResource(R.string.nutriment_carbohydrates),
                    value = diaryDay.totalCarbohydrates,
                    goal = diaryDay.dailyGoals.carbohydratesAsGrams,
                    progressColor = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                NutrimentIndicator(
                    title = stringResource(R.string.nutriment_fats),
                    value = diaryDay.totalFats,
                    goal = diaryDay.dailyGoals.fatsAsGrams,
                    progressColor = nutrientsPalette.fatsOnSurfaceContainer
                )
            }
        }
    }
}

@Composable
private fun NutrimentIndicator(
    title: String,
    value: Int,
    goal: Int,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val gramShort = stringResource(R.string.unit_gram_short)

    val valueGoalString = remember(typography, colorScheme, gramShort, title, value, goal) {
        val valueStatus = value withGoal goal

        buildAnnotatedString {
            withStyle(
                typography.headlineSmall.merge(
                    color = when (valueStatus) {
                        ValueStatus.Exceeded -> colorScheme.error
                        ValueStatus.Remaining,
                        ValueStatus.Achieved -> progressColor
                    }
                ).toSpanStyle()
            ) {
                append(value.toString())
            }
            withStyle(
                typography.bodyMedium.merge(
                    color = colorScheme.outline
                ).toSpanStyle()
            ) {
                append(" / $goal $gramShort")
            }
        }
    }

    val animatedValue by animateIntAsState(value)
    val animatedGoal by animateIntAsState(goal)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
            ) {
                drawRect(
                    color = progressColor
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = valueGoalString,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (animatedValue > animatedGoal) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                trackColor = progressColor,
                color = colorScheme.error,
                progress = {
                    if (animatedGoal == 0) {
                        1f
                    } else {
                        (animatedValue - animatedGoal) / animatedGoal.toFloat()
                    }
                },
                drawStopIndicator = {}
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                progress = {
                    if (animatedGoal == 0) 1f else animatedValue / animatedGoal.toFloat()
                }
            )
        }
    }
}

private enum class ValueStatus {
    Remaining,
    Achieved,
    Exceeded
}

private infix fun <N : Comparable<N>> N.withGoal(goal: N) = when {
    this < goal -> Remaining
    this > goal -> Exceeded
    else -> Achieved
}

@Composable
fun CaloriesCardSkeleton(
    modifier: Modifier = Modifier,
    shimmerInstance: Shimmer = rememberShimmer(ShimmerBounds.View)
) {
    FoodYouHomeCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(100.dp, MaterialTheme.typography.titleLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(160.dp, MaterialTheme.typography.headlineLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .height(16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(100.dp, MaterialTheme.typography.labelLarge.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NutrimentIndicatorSkeleton(shimmerInstance)
                NutrimentIndicatorSkeleton(shimmerInstance)
                NutrimentIndicatorSkeleton(shimmerInstance)
            }
        }
    }
}

@Composable
private fun NutrimentIndicatorSkeleton(shimmerInstance: Shimmer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(60.dp, MaterialTheme.typography.titleMedium.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(60.dp, MaterialTheme.typography.headlineSmall.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }

        Box(
            modifier = Modifier
                .shimmer(shimmerInstance)
                .height(4.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CaloriesCardSkeletonPreview() {
    FoodYouTheme {
        CaloriesCardSkeleton()
    }
}

@Preview
@Composable
private fun CaloriesCardPreview() {
    FoodYouTheme {
        CaloriesCard(
            diaryDay = DiaryDayPreviewParameterProvider().values.first()
        )
    }
}
