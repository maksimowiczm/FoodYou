package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.form.validateDouble
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuantityInput(
    entries: List<QuantityType>,
    selectedQuantity: QuantityType,
    onQuantity: (QuantityType) -> Unit,
    formField: FormField,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val inputColor by
        animateColorAsState(
            if (formField.error == null) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        )
    val contentColor by
        animateColorAsState(
            if (formField.error == null) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onErrorContainer,
            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        )

    val focusRequester = remember { FocusRequester() }
    val inputInteractionSource = remember { MutableInteractionSource() }
    val inputIsPressed = inputInteractionSource.collectIsPressedAsState()
    val inputProgress =
        animateFloatAsState(
            if (inputIsPressed.value) 1f else 0f,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    val quantityInteractionSource = remember { MutableInteractionSource() }
    val quantityIsPressed = quantityInteractionSource.collectIsPressedAsState()
    val quantityProgress =
        animateFloatAsState(
            if (quantityIsPressed.value || expanded) 1f else 0f,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Surface(
            onClick = { focusRequester.requestFocus() },
            modifier =
                Modifier.height(48.dp).width(120.dp).graphicsLayer {
                    clip = true
                    shape =
                        RoundedCornerShape(
                            topStart = lerp(16.dp, 8.dp, inputProgress.value),
                            bottomStart = lerp(16.dp, 8.dp, inputProgress.value),
                            topEnd = lerp(12.dp, 8.dp, inputProgress.value),
                            bottomEnd = lerp(12.dp, 8.dp, inputProgress.value),
                        )
                },
            color = inputColor,
            contentColor = contentColor,
            interactionSource = inputInteractionSource,
        ) {
            BasicTextField(
                state = formField.textFieldState,
                modifier =
                    Modifier.height(48.dp)
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = LocalTextStyle.current.merge(LocalContentColor.current),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(LocalContentColor.current),
                decorator = { Box(contentAlignment = Alignment.CenterStart) { it() } },
            )
        }
        Surface(
            onClick = { expanded = true },
            modifier =
                Modifier.heightIn(min = 48.dp).weight(1f).graphicsLayer {
                    clip = true
                    shape =
                        RoundedCornerShape(
                            topStart = lerp(12.dp, 8.dp, quantityProgress.value),
                            bottomStart = lerp(12.dp, 8.dp, quantityProgress.value),
                            topEnd = lerp(16.dp, 8.dp, quantityProgress.value),
                            bottomEnd = lerp(16.dp, 8.dp, quantityProgress.value),
                        )
                },
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            interactionSource = quantityInteractionSource,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedQuantity.stringResource(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    val arrowRotation =
                        animateFloatAsState(
                            targetValue = if (expanded) 180f else 0f,
                            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                        )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { rotationZ = arrowRotation.value },
                    )

                    QuantityDropdown(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        entries = entries,
                        selectedQuantity = selectedQuantity,
                        onQuantity = onQuantity,
                    )
                }
            }
        }
    }
}

enum class QuantityType {
    Gram,
    Ounce,
    FluidOunce,
    Milliliter,
    Serving,
    Package;

    @Composable
    fun stringResource(): String =
        when (this) {
            Gram -> stringResource(Res.string.unit_gram_short)
            Ounce -> stringResource(Res.string.unit_ounce_short)
            Milliliter -> stringResource(Res.string.unit_milliliter_short)
            FluidOunce -> stringResource(Res.string.unit_fluid_ounce_short)
            Serving -> stringResource(Res.string.product_serving)
            Package -> stringResource(Res.string.product_package)
        }
}

@Composable
fun rememberQuantityFormField(vararg keys: Any?, defaultValue: String? = null): FormField {
    val invalidNumber = stringResource(Res.string.error_invalid_number)
    val valueMustBePositive = stringResource(Res.string.error_value_must_be_positive)

    return rememberFormField(*keys, defaultValue = defaultValue) {
        validateDouble {
            constrain(invalidNumber) { it != null }
            constrain(valueMustBePositive) { it?.let { it > 0 } ?: true }
        }
    }
}

@Composable
private fun QuantityDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    entries: List<QuantityType>,
    selectedQuantity: QuantityType,
    onQuantity: (QuantityType) -> Unit,
) {
    DropdownMenuPopup(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, entries.size)) {
            entries.forEachIndexed { i, entry ->
                DropdownMenuItem(
                    selected = entry == selectedQuantity,
                    onClick = { onQuantity(entry) },
                    text = { Text(entry.stringResource()) },
                    shapes = MenuDefaults.itemShape(i, entries.size),
                    selectedLeadingIcon = {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                    },
                )
                if (i != entries.lastIndex) {
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun QuantityInputPreview() {
    var quantity by remember { mutableStateOf(QuantityType.Gram) }

    PreviewFoodYouTheme {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
            QuantityInput(
                entries = QuantityType.entries,
                selectedQuantity = quantity,
                onQuantity = { quantity = it },
                formField = rememberQuantityFormField(),
            )
        }
    }
}
