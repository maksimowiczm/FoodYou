package com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalDiaryPalette
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalNutrimentsPalette
import com.maksimowiczm.foodyou.core.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.core.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.core.ui.toggle
import kotlin.math.abs
import kotlin.math.max

@Composable
fun CaloriesCard(
    diaryDay: DiaryDay,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val calories = diaryDay.totalCalories
    val goal = diaryDay.dailyGoals.calories
    val valueStatus = ValueStatus.fromCalories(calories, goal)
    val left = abs(goal - calories)

    val nutrimentsPalette = LocalNutrimentsPalette.current
    val diaryPalette = LocalDiaryPalette.current
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val kcalSuffix = stringResource(R.string.unit_kcal)

    val annotatedString = remember(calories, goal) {
        buildAnnotatedString {
            withStyle(
                typography.headlineLarge.copy(
                    color = when (valueStatus) {
                        ValueStatus.Exceeded -> colorScheme.error
                        ValueStatus.Remaining -> colorScheme.onSurface
                        ValueStatus.Achieved -> diaryPalette.goalsFulfilledColor
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

    val hapticFeedback = LocalHapticFeedback.current
    val hapticOnExpandedChange: (Boolean) -> Unit = {
        hapticFeedback.toggle(it)
        onExpandedChange(it)
    }

    ElevatedCard(
        modifier = modifier,
        onClick = { hapticOnExpandedChange(!expanded) }
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

            Text(annotatedString)

            Spacer(Modifier.height(8.dp))

            val max = max(goal, calories)
            val animatedMax by animateFloatAsState(max.toFloat())
            MultiColorProgressIndicator(
                items = listOf(
                    MultiColorProgressIndicatorItem(
                        progress = animatedProteins / animatedMax,
                        color = nutrimentsPalette.proteinsOnSurfaceContainer
                    ),
                    MultiColorProgressIndicatorItem(
                        progress = animatedCarbohydrates / animatedMax,
                        color = nutrimentsPalette.carbohydratesOnSurfaceContainer
                    ),
                    MultiColorProgressIndicatorItem(
                        progress = animatedFats / animatedMax,
                        color = nutrimentsPalette.fatsOnSurfaceContainer
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
                    color = LocalDiaryPalette.current.goalsFulfilledColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (expanded) {
                Spacer(Modifier.height(16.dp))

                CaloriesCardExpansion(
                    diaryDay = diaryDay
                )
            }
        }
    }
}

@Composable
private fun CaloriesCardExpansion(
    diaryDay: DiaryDay,
    modifier: Modifier = Modifier
) {
    val nutrimentsPalette = LocalNutrimentsPalette.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExpansionItem(
            value = diaryDay.totalCaloriesProteins,
            goal = diaryDay.dailyGoals.proteinsAsCalories,
            color = nutrimentsPalette.proteinsOnSurfaceContainer,
            title = stringResource(R.string.nutriment_proteins),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        ExpansionItem(
            value = diaryDay.totalCaloriesCarbohydrates,
            goal = diaryDay.dailyGoals.carbohydratesAsCalories,
            color = nutrimentsPalette.carbohydratesOnSurfaceContainer,
            title = stringResource(R.string.nutriment_carbohydrates),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        ExpansionItem(
            value = diaryDay.totalCaloriesFats,
            goal = diaryDay.dailyGoals.fatsAsCalories,
            color = nutrimentsPalette.fatsOnSurfaceContainer,
            title = stringResource(R.string.nutriment_fats),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun ExpansionItem(
    value: Int,
    goal: Int,
    color: Color,
    title: String,
    modifier: Modifier = Modifier
) {
    val kcal = stringResource(R.string.unit_kcal)
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val diaryPalette = LocalDiaryPalette.current
    val valueStatus = ValueStatus.fromCalories(value, goal)

    val annotatedString = remember(value, goal) {
        buildAnnotatedString {
            withStyle(
                typography.headlineSmall.toSpanStyle().copy(
                    color = when (valueStatus) {
                        ValueStatus.Exceeded -> colorScheme.error
                        ValueStatus.Remaining -> colorScheme.onSurface
                        ValueStatus.Achieved -> diaryPalette.goalsFulfilledColor
                    }
                )
            ) {
                append(value.toString())
            }
            withStyle(
                typography.bodyMedium.merge(
                    color = colorScheme.outline
                ).toSpanStyle()
            ) {
                append(" / $goal $kcal")
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
                    .size(20.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = annotatedString
        )
    }
}

private enum class ValueStatus {
    Remaining,
    Achieved,
    Exceeded;

    companion object {
        fun fromCalories(calories: Int, goal: Int) = when {
            calories < goal -> Remaining
            calories > goal -> Exceeded
            else -> Achieved
        }
    }
}

@PreviewLightDark
@Preview(
    device = Devices.TABLET
)
@Composable
private fun CaloriesCardRemainingPreview(
    @PreviewParameter(BooleanPreviewParameter::class) expanded: Boolean
) {
    FoodYouTheme {
        CaloriesCard(
            diaryDay = DiaryDayPreviewParameterProvider().values.first(),
            expanded = expanded,
            onExpandedChange = {}
        )
    }
}
