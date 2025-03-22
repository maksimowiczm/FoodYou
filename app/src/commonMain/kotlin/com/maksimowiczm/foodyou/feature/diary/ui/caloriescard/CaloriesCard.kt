package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.HomeState
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CaloriesCard(
    homeState: HomeState,
    modifier: Modifier = Modifier,
    viewModel: CaloriesCardViewModel = koinViewModel()
) {
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val state by viewModel.state.collectAsStateWithLifecycle()

    val dd = diaryDay

    Crossfade(
        targetState = dd != null,
        modifier = modifier
    ) {
        if (it && dd != null) {
            CaloriesCard(
                diaryDay = dd,
                state = state,
                toggleState = viewModel::toggleCaloriesCardState
            )
        } else {
            CaloriesCardSkeleton(
                state = state,
                toggleState = viewModel::toggleCaloriesCardState,
                shimmerInstance = homeState.shimmer
            )
        }
    }
}

@Composable
private fun CaloriesCard(
    diaryDay: DiaryDay,
    state: CaloriesCardState,
    toggleState: () -> Unit,
    modifier: Modifier = Modifier
) {
    CaloriesCardLayout(
        state = state,
        toggleState = toggleState,
        header = {
            Header(state) {
                Text(
                    text = stringResource(Res.string.unit_calories),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        compactContent = { Compact(diaryDay) },
        expandedContent = { Expanded(diaryDay) },
        modifier = modifier
    )
}

@Composable
private fun ColumnScope.Compact(diaryDay: DiaryDay) {
    val calories = diaryDay.totalCalories.roundToInt()
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
    val kcalSuffix = stringResource(Res.string.unit_kcal)

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
                Res.plurals.negative_exceeded_by_calories,
                left,
                left
            ),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        ValueStatus.Remaining -> Text(
            text = pluralStringResource(
                Res.plurals.neutral_remaining_calories,
                left,
                left
            ),
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        ValueStatus.Achieved -> Text(
            text = stringResource(Res.string.positive_goal_reached),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun ColumnScope.Expanded(diaryDay: DiaryDay) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientIndicator(
            title = stringResource(Res.string.nutriment_proteins),
            value = diaryDay.totalProteins.roundToInt(),
            goal = diaryDay.dailyGoals.proteinsAsGrams,
            progressColor = nutrientsPalette.proteinsOnSurfaceContainer
        )

        NutrientIndicator(
            title = stringResource(Res.string.nutriment_carbohydrates),
            value = diaryDay.totalCarbohydrates.roundToInt(),
            goal = diaryDay.dailyGoals.carbohydratesAsGrams,
            progressColor = nutrientsPalette.carbohydratesOnSurfaceContainer
        )

        NutrientIndicator(
            title = stringResource(Res.string.nutriment_fats),
            value = diaryDay.totalFats.roundToInt(),
            goal = diaryDay.dailyGoals.fatsAsGrams,
            progressColor = nutrientsPalette.fatsOnSurfaceContainer
        )
    }
}

@Composable
private fun NutrientIndicator(
    title: String,
    value: Int,
    goal: Int,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val gramShort = stringResource(Res.string.unit_gram_short)

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
    this < goal -> ValueStatus.Remaining
    this > goal -> ValueStatus.Exceeded
    else -> ValueStatus.Achieved
}
