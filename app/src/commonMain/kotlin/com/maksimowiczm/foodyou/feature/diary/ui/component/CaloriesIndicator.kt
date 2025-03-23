package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.NutrientsHelper
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus.Companion.asValueStatus
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicator
import com.maksimowiczm.foodyou.ui.component.MultiColorProgressIndicatorItem
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.abs
import kotlin.math.max
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CaloriesIndicator(
    calories: Int,
    caloriesGoal: Int,
    proteins: Int,
    carbohydrates: Int,
    fats: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.unit_calories),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = rememberCaloriesString(calories, caloriesGoal),
            // MUST use style because otherwise the text won't have the correct height
            // (because of the AnnotatedString wierd behavior?)
            style = MaterialTheme.typography.headlineLarge
        )

        CaloriesProgressIndicator(
            calories = calories.toFloat(),
            proteins = NutrientsHelper.proteinsToCalories(proteins.toFloat()),
            carbohydrates = NutrientsHelper.carbohydratesToCalories(carbohydrates.toFloat()),
            fats = NutrientsHelper.fatsToCalories(fats.toFloat()),
            goal = caloriesGoal.toFloat()
        )

        CaloriesLabel(
            calories = calories,
            goal = caloriesGoal
        )
    }
}

@Composable
fun CaloriesIndicatorSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Calories
        Spacer(
            modifier = Modifier
                .shimmer(shimmer)
                .width(100.dp)
                .height(MaterialTheme.typography.titleLarge.toDp())
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )

        // Calories value
        Spacer(
            modifier = Modifier
                .shimmer(shimmer)
                .size(160.dp, MaterialTheme.typography.headlineLarge.toDp())
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )

        // Calories progress
        Spacer(
            modifier = Modifier
                .shimmer(shimmer)
                .height(16.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )

        // Calories label
        Spacer(
            modifier = Modifier
                .shimmer(shimmer)
                .size(150.dp, MaterialTheme.typography.labelLarge.toDp())
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Composable
private fun rememberCaloriesString(calories: Int, goal: Int): AnnotatedString {
    val valueStatus by remember(calories, goal) {
        derivedStateOf { calories.asValueStatus(goal) }
    }
    val left by remember(calories, goal) {
        derivedStateOf { abs(goal - calories) }
    }

    val nutrientsPalette = LocalNutrientsPalette.current
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val kcalSuffix = stringResource(Res.string.unit_kcal)

    return remember(valueStatus, left, nutrientsPalette, typography, colorScheme, kcalSuffix) {
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
}

@Composable
private fun CaloriesProgressIndicator(
    calories: Float,
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    goal: Float
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
        modifier = Modifier
            .height(16.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun CaloriesLabel(calories: Int, goal: Int) {
    val valueStatus by remember(calories, goal) {
        derivedStateOf { calories.asValueStatus(goal) }
    }

    val left by remember(calories, goal) {
        derivedStateOf { abs(goal - calories) }
    }

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
