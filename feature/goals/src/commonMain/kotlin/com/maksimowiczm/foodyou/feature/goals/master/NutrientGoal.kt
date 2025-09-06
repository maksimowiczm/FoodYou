package com.maksimowiczm.foodyou.feature.goals.master

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.food.NutrientValue
import com.maksimowiczm.foodyou.shared.food.NutritionFactsField
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.shared.ui.utils.LocalEnergyFormatter
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NutrientGoal(
    field: NutritionFactsField,
    value: NutrientValue,
    target: Double,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    suffix: String = stringResource(Res.string.unit_gram_short),
) {
    NutrientGoal(
        label = field.stringResource(),
        target =
            NutrientGoalDefaults.simpleTargetString(
                value = value,
                target = target,
                color = color,
                suffix = suffix,
            ),
        color = color,
        state = rememberNutrientGoalState(value, target),
        modifier = modifier,
    )
}

@Composable
internal fun NutrientGoal(
    label: String,
    target: AnnotatedString,
    color: Color,
    state: NutrientGoalState,
    modifier: Modifier = Modifier,
) {
    NutrientGoal(
        label = {
            val color = if (state.isExceeded) MaterialTheme.colorScheme.error else color

            Text(text = label, style = LocalTextStyle.current.copy(color = color))
        },
        value = { Text(target) },
        progressColor = color.copy(alpha = .9f),
        state = state,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun NutrientGoal(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    progressColor: Color,
    state: NutrientGoalState,
    modifier: Modifier = Modifier,
) {
    val progress by
        animateFloatAsState(
            targetValue = state.progress % 1,
            animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        )
    val progressBarColor by
        animateColorAsState(
            if (state.progress > 1) MaterialTheme.colorScheme.error else progressColor
        )
    val trackColor by
        animateColorAsState(
            if (state.progress > 1) progressColor.copy(alpha = .75f)
            else progressColor.copy(alpha = 0.25f)
        )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            label()
            value()
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = progressBarColor,
            trackColor = trackColor,
            drawStopIndicator = {},
        )
    }
}

@Composable
internal fun rememberNutrientGoalState(value: NutrientValue, target: Double): NutrientGoalState =
    remember(value, target) { NutrientGoalState(value = value.value ?: 0.0, target = target) }

@Immutable
internal class NutrientGoalState(val value: Double, val target: Double) {
    val isExceeded: Boolean
        get() = value > target

    val progress: Float
        get() = if (target == 0.0) 0f else (value / target).toFloat().coerceIn(0f, 1.99999f)
}

internal object NutrientGoalDefaults {

    @Composable
    fun energyTargetString(
        value: NutrientValue,
        target: Double,
        color: Color = MaterialTheme.colorScheme.primary,
    ): AnnotatedString {
        val energyFormatter = LocalEnergyFormatter.current

        val value = value.value ?: 0.0
        val isExceeded = value > target
        val colorScheme = MaterialTheme.colorScheme
        val localStyle = LocalTextStyle.current

        return buildAnnotatedString {
            withStyle(
                localStyle.copy(color = if (isExceeded) colorScheme.error else color).toSpanStyle()
            ) {
                append(energyFormatter.formatEnergy(value, withSuffix = false))
            }
            withStyle(localStyle.copy(color = colorScheme.outline).toSpanStyle()) {
                append(" / ")
                append(energyFormatter.formatEnergy(target, withSuffix = true))
            }
        }
    }

    @Composable
    fun simpleTargetString(
        value: NutrientValue,
        target: Double,
        color: Color,
        suffix: String,
    ): AnnotatedString {
        val isComplete = value.isComplete
        val value = value.value ?: 0.0
        val isExceeded = value > target
        val colorScheme = MaterialTheme.colorScheme
        val localStyle = LocalTextStyle.current

        return remember(
            value,
            target,
            color,
            suffix,
            localStyle,
            colorScheme,
            isExceeded,
            isComplete,
        ) {
            buildAnnotatedString {
                withStyle(
                    localStyle
                        .copy(color = if (isExceeded) colorScheme.error else color)
                        .toSpanStyle()
                ) {
                    if (!isComplete) {
                        append("* ")
                    }
                    append(value.formatClipZeros())
                }
                withStyle(localStyle.copy(color = colorScheme.outline).toSpanStyle()) {
                    append(" / ")
                    append(target.formatClipZeros())
                    append(" ")
                    append(suffix)
                }
            }
        }
    }
}
