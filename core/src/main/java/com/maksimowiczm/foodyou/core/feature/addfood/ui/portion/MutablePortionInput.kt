package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import kotlin.math.max

@Composable
fun MutableWeightUnitPortionInput(
    portion: MutableWeightUnitPortion,
    onConfirm: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(portion.quantity.toString()))
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                val text = it.text.trim()
                if (text.length < 6) {
                    textFieldValue = TextFieldValue(
                        text = text,
                        selection = it.selection
                    )

                    portion.onWeightChange(text)
                }
            },
            modifier = Modifier.weight(2f),
            isError = portion.error != null,
            suffix = portion.weightUnit.let {
                {
                    Text(
                        text = it.stringResourceShort(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            maxLines = 1,
            shape = MaterialTheme.shapes.medium
        )

        Text(
            modifier = Modifier.weight(2f),
            text = portion.calories.toString() + stringResource(R.string.unit_kcal),
            textAlign = TextAlign.Center
        )

        FilledIconButton(
            onClick = { onConfirm(portion.quantity) },
            enabled = portion.error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.action_add)
            )
        }
    }
}

@Composable
fun MutableDefinedPortionInput(
    portion: MutableDefinedPortion,
    onConfirm: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue("%.0f".format(portion.quantity)))
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                val text = it.text.trim()

                if (text.length < 3 || (text.length <= 3 && text.contains('.'))) {
                    textFieldValue = TextFieldValue(
                        text = text,
                        selection = it.selection
                    )
                    portion.onWeightChange(it.text)
                }
            },
            modifier = Modifier.weight(2f),
            isError = portion.error != null,
            suffix = portion.weightUnit.let {
                {
                    Text(
                        text = portion.label,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            maxLines = 1,
            shape = MaterialTheme.shapes.medium
        )

        AdaptiveRow(
            modifier = Modifier.weight(2f),
            portion = portion
        )

        FilledIconButton(
            onClick = { onConfirm(portion.quantity) },
            enabled = portion.error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.action_add)
            )
        }
    }
}

// TODO
// This looks more complicated than it should be especially with the animations. I'm not sure if
// it's done correctly but it works for now.
@Composable
fun AdaptiveRow(portion: MutableDefinedPortion, modifier: Modifier = Modifier) {
    val weightString = portion.weight.toString() + portion.weightUnit.stringResourceShort()
    val weight = @Composable {
        Text(
            text = weightString,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }

    val caloriesString = portion.calories.toString() + stringResource(R.string.unit_kcal)
    val calories = @Composable {
        Text(
            text = caloriesString,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }

    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()

    val state = rememberSaveable(
        saver = AdaptiveRowState.saver
    ) {
        AdaptiveRowState()
    }
    val weightMultiplier by state.multiplier

    Layout(
        contents = listOf(weight, calories),
        modifier = modifier.clipToBounds()
    ) { (weightMeasurable, caloriesMeasurable), constraints ->
        @Suppress("NAME_SHADOWING")
        val weightMeasurable = weightMeasurable.first()

        @Suppress("NAME_SHADOWING")
        val caloriesMeasurable = caloriesMeasurable.first()

        val caloriesMeasure = textMeasurer.measure(
            text = caloriesString,
            style = textStyle
        )

        val weightMeasure = textMeasurer.measure(
            text = weightString,
            style = textStyle
        )

        val halfWidth = constraints.maxWidth / 2

        // Measure if both child will fit
        val newShowBoth =
            caloriesMeasure.size.width + weightMeasure.size.width <= constraints.maxWidth &&
                caloriesMeasure.size.width <= halfWidth &&
                weightMeasure.size.width <= halfWidth

        state.update(newShowBoth, weightMultiplier)

        val layout = if (state.animate) {
            animatedLayout(
                constraints = constraints,
                weightMeasurable = weightMeasurable,
                caloriesMeasurable = caloriesMeasurable,
                weightMultiplier = weightMultiplier
            )
        } else if (state.showBoth) {
            splitLayout(
                constraints = constraints,
                weightMeasurable = weightMeasurable,
                caloriesMeasurable = caloriesMeasurable
            )
        } else {
            singleLayout(
                constraints = constraints,
                caloriesMeasurable = caloriesMeasurable
            )
        }

        layout
    }
}

private fun MeasureScope.splitLayout(
    constraints: Constraints,
    weightMeasurable: Measurable,
    caloriesMeasurable: Measurable
): MeasureResult {
    val halfWidth = constraints.maxWidth / 2

    val halfWidthConstrains = constraints.copy(
        minWidth = halfWidth,
        maxWidth = halfWidth
    )

    val weightPlaceable = weightMeasurable.measure(halfWidthConstrains)
    val caloriesPlaceable = caloriesMeasurable.measure(halfWidthConstrains)

    return layout(constraints.maxWidth, max(weightPlaceable.height, caloriesPlaceable.height)) {
        weightPlaceable.placeRelative(0, 0)
        caloriesPlaceable.placeRelative(halfWidth, 0)
    }
}

private fun MeasureScope.singleLayout(
    constraints: Constraints,
    caloriesMeasurable: Measurable
): MeasureResult {
    val caloriesPlaceable = caloriesMeasurable.measure(constraints)

    return layout(constraints.maxWidth, caloriesPlaceable.height) {
        caloriesPlaceable.placeRelative(0, 0)
    }
}

private fun MeasureScope.animatedLayout(
    constraints: Constraints,
    weightMeasurable: Measurable,
    caloriesMeasurable: Measurable,
    weightMultiplier: Float
): MeasureResult {
    val halfWidth = constraints.maxWidth / 2

    val weightOffset = halfWidth * (1 - weightMultiplier)
    val caloriesWidth = halfWidth + halfWidth * (1 - weightMultiplier)

    val weightPlaceable = weightMeasurable.measure(
        constraints.copy(
            minWidth = halfWidth,
            maxWidth = halfWidth
        )
    )
    val caloriesPlaceable = caloriesMeasurable.measure(
        constraints.copy(
            minWidth = caloriesWidth.toInt(),
            maxWidth = caloriesWidth.toInt()
        )
    )

    return layout(constraints.maxWidth, max(weightPlaceable.height, caloriesPlaceable.height)) {
        weightPlaceable.placeRelative(0 - weightOffset.toInt(), 0)
        caloriesPlaceable.placeRelative(halfWidth - weightOffset.toInt(), 0)
    }
}

private class AdaptiveRowState(
    private var firstFrame: Boolean = true,
    initialShowBoth: Boolean = true
) {
    companion object {
        val saver: Saver<AdaptiveRowState, Boolean>
            get() = Saver(
                save = {
                    it.showBoth
                },
                restore = {
                    AdaptiveRowState(
                        firstFrame = false,
                        initialShowBoth = it
                    )
                }
            )
    }

    var showBoth by mutableStateOf(initialShowBoth)
    var animateTo0 by mutableStateOf(false)
    var animateTo1 by mutableStateOf(false)
    val animate by derivedStateOf {
        animateTo0 || animateTo1
    }

    val multiplier
        @Composable get() = animateFloatAsState(
            targetValue = if (showBoth) 1f else 0f,
            animationSpec = tween(
                durationMillis = 200
            )
        )

    fun update(newShowBoth: Boolean, multiplier: Float) {
        if (firstFrame) {
            firstFrame = false
            showBoth = newShowBoth
            return
        }

        if (animateTo1 && multiplier == 1f) {
            animateTo1 = false
        } else if (animateTo0 && multiplier == 0f) {
            animateTo0 = false
        }

        if (showBoth != newShowBoth) {
            if (newShowBoth) {
                animateTo1 = true
            } else {
                animateTo0 = true
            }
        }

        showBoth = newShowBoth
    }
}

@Preview
@Composable
private fun MutableWeightUnitPortionInputPreview() {
    FoodYouTheme {
        Surface {
            MutableWeightUnitPortionInput(
                portion = MutableWeightUnitPortion(
                    initialWeight = 100,
                    weightUnit = WeightUnit.Gram,
                    calculateCalories = { 100 }
                ),
                onConfirm = {}
            )
        }
    }
}

@Preview
@Preview(
    device = "spec:width=250dp,height=800dp,dpi=240"
)
@Composable
private fun MutableDefinedPortionInputPreview() {
    FoodYouTheme {
        Surface {
            MutableDefinedPortionInput(
                portion = MutableDefinedPortion(
                    initialQuantity = 1f,
                    weightUnit = WeightUnit.Gram,
                    calculateWeight = { 120 },
                    calculateCalories = { 345 },
                    portionType = PortionType.Package
                ),
                onConfirm = {}
            )
        }
    }
}
