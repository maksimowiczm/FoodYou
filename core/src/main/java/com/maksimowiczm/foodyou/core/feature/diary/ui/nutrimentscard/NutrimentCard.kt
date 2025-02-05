package com.maksimowiczm.foodyou.core.feature.diary.ui.nutrimentscard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.ui.theme.LocalDiaryPalette
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import kotlin.math.abs

private enum class ValueStatus {
    Remaining,
    Achieved,
    Exceeded
}

@Composable
fun NutrimentCard(
    text: String,
    value: Int,
    goalValue: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val valueStatus = when {
        value < goalValue -> ValueStatus.Remaining
        value > goalValue -> ValueStatus.Exceeded
        else -> ValueStatus.Achieved
    }
    val left = abs(goalValue - value)

    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
                .width(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = text,
                color = color,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            val progressSize = 96.dp * LocalDensity.current.fontScale
            Progress(
                value = value,
                goalValue = goalValue,
                color = color,
                modifier = Modifier.size(progressSize)
            )

            when (valueStatus) {
                ValueStatus.Exceeded -> Text(
                    text = pluralStringResource(
                        R.plurals.negative_exceeded_by_grams,
                        left,
                        left
                    ),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )

                ValueStatus.Remaining -> Text(
                    text = pluralStringResource(
                        R.plurals.neutral_remaining_grams,
                        left,
                        left
                    ),
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )

                ValueStatus.Achieved -> Text(
                    text = stringResource(R.string.positive_goal_reached),
                    color = LocalDiaryPalette.current.goalsFulfilledColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun Progress(
    value: Int,
    goalValue: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat()
    )
    val animatedLeft = abs(animatedValue - goalValue)
    val animatedStatus = when {
        animatedValue < goalValue -> ValueStatus.Remaining
        animatedValue > goalValue -> ValueStatus.Exceeded
        else -> ValueStatus.Achieved
    }
    val valueStatus = when {
        value < goalValue -> ValueStatus.Remaining
        value > goalValue -> ValueStatus.Exceeded
        else -> ValueStatus.Achieved
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .width(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        if (animatedStatus == ValueStatus.Exceeded) {
            CircularProgressIndicator(
                progress = { animatedLeft / goalValue },
                modifier = Modifier.matchParentSize(),
                color = MaterialTheme.colorScheme.error,
                trackColor = color,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp
            )
        } else {
            CircularProgressIndicator(
                progress = { animatedValue / goalValue },
                modifier = Modifier.matchParentSize(),
                color = color,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                color = when (valueStatus) {
                    ValueStatus.Exceeded -> MaterialTheme.colorScheme.error
                    else -> color
                },
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "/$goalValue" + stringResource(R.string.unit_gram_short),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Preview(
    fontScale = 2f
)
@Composable
private fun NutrimentCardPreview() {
    FoodYouTheme {
        NutrimentCard(
            text = "Protein",
            value = 100,
            goalValue = 100,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
