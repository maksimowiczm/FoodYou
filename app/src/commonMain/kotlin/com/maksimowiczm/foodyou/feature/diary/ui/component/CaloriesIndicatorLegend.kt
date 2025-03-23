package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus.Companion.asValueStatus
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.unit_gram_short
import org.jetbrains.compose.resources.stringResource

/**
 * Legend for the calories indicator. Suffix "g" is added to the values.
 */
@Composable
fun CaloriesIndicatorLegend(
    proteins: Int,
    proteinsGoal: Int,
    carbohydrates: Int,
    carbohydratesGoal: Int,
    fats: Int,
    fatsGoal: Int,
    modifier: Modifier = Modifier.Companion
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientIndicator(
            title = stringResource(Res.string.nutriment_proteins),
            value = proteins,
            goal = proteinsGoal,
            progressColor = nutrientsPalette.proteinsOnSurfaceContainer
        )

        NutrientIndicator(
            title = stringResource(Res.string.nutriment_carbohydrates),
            value = carbohydrates,
            goal = carbohydratesGoal,
            progressColor = nutrientsPalette.carbohydratesOnSurfaceContainer
        )

        NutrientIndicator(
            title = stringResource(Res.string.nutriment_fats),
            value = fats,
            goal = fatsGoal,
            progressColor = nutrientsPalette.fatsOnSurfaceContainer
        )
    }
}

@Composable
fun NutrientIndicatorLegendSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NutrientIndicatorSkeleton(shimmer)
        NutrientIndicatorSkeleton(shimmer)
        NutrientIndicatorSkeleton(shimmer)
    }
}

@Composable
private fun NutrientIndicator(
    title: String,
    value: Int,
    goal: Int,
    progressColor: Color,
    modifier: Modifier = Modifier.Companion
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val gramShort = stringResource(Res.string.unit_gram_short)

    val valueGoalString = remember(typography, colorScheme, gramShort, title, value, goal) {
        val valueStatus = value.asValueStatus(goal)

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
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Canvas(
                modifier = Modifier.Companion
                    .size(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
            ) {
                drawRect(
                    color = progressColor
                )
            }

            Spacer(Modifier.Companion.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.Companion.weight(1f))

            Text(
                text = valueGoalString,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (animatedValue > animatedGoal) {
            LinearProgressIndicator(
                modifier = Modifier.Companion.fillMaxWidth(),
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
                modifier = Modifier.Companion.fillMaxWidth(),
                color = progressColor,
                progress = {
                    if (animatedGoal == 0) 1f else animatedValue / animatedGoal.toFloat()
                }
            )
        }
    }
}

@Composable
private fun NutrientIndicatorSkeleton(shimmer: Shimmer, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color indicator
                Spacer(
                    modifier = Modifier
                        .shimmer(shimmer)
                        .size(16.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )

                Spacer(Modifier.Companion.width(8.dp))

                // Title
                Spacer(
                    modifier = Modifier
                        .shimmer(shimmer)
                        .size(100.dp, MaterialTheme.typography.titleMedium.toDp())
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }

            Spacer(Modifier.weight(1f))

            // Value / Goal
            Spacer(
                modifier = Modifier
                    .shimmer(shimmer)
                    .size(100.dp, MaterialTheme.typography.headlineSmall.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }

        // Progress
        Spacer(
            modifier = Modifier
                .shimmer(shimmer)
                .height(4.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}
