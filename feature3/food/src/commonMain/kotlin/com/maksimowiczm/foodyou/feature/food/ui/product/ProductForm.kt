package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(
    state: ProductFormState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        end = contentPadding.calculateStartPadding(layoutDirection)
    )
    val verticalPadding = PaddingValues(
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    )

    Column(
        modifier = modifier.padding(verticalPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.headline_general),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.name.TextField(
            label = stringResource(Res.string.product_name),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            required = true,
            imeAction = ImeAction.Next,
            suffix = null
        )

        state.brand.TextField(
            label = stringResource(Res.string.product_brand),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            imeAction = ImeAction.Next
        )

        BarcodeTextField(
            state = state.barcode,
            onBarcodeScanner = {
                // TODO
            },
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            imeAction = ImeAction.Next
        )

        state.note.TextField(
            label = stringResource(Res.string.headline_note),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            imeAction = ImeAction.Next,
            supportingText = stringResource(Res.string.description_add_note)
        )

        MeasurementPicker(
            selected = state.measurement,
            onSelect = { state.measurement = it },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontalPadding)
                .fillMaxWidth()
        )
    }
}

@Composable
private inline fun <reified T> FormField<T, ProductFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    imeAction: ImeAction? = null,
    suffix: String? = stringResource(Res.string.unit_gram_short)
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        supportingText = {
            val error = this.error
            if (error != null) {
                Text(error.stringResource())
            } else if (required) {
                Text(stringResource(Res.string.neutral_required))
            }
        },
        suffix = suffix?.let { { Text(suffix) } },
        isError = error != null,
        keyboardOptions = if (T::class == Float::class) {
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction ?: ImeAction.Next
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction ?: ImeAction.Next
            )
        }
    )
}

@Composable
private inline fun <reified T> FormField<T, Nothing>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction? = null,
    supportingText: String? = null
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = if (T::class == Float::class) {
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction ?: ImeAction.Next
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction ?: ImeAction.Next
            )
        }
    )
}

@Composable
private fun BarcodeTextField(
    state: FormField<String?, Nothing>,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        state = state.textFieldState,
        modifier = modifier,
        label = { Text(stringResource(Res.string.product_barcode)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        trailingIcon = {
            FilledTonalIconButton(
                onClick = onBarcodeScanner
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_barcode_scanner),
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MeasurementPicker(
    selected: Measurement,
    onSelect: (Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val possibleValues = remember {
        listOf(
            Measurement.Gram(100f),
            Measurement.Milliliter(100f),
            Measurement.Serving(1f),
            Measurement.Package(1f)
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.headline_values_per),
            style = MaterialTheme.typography.bodyLarge
        )

        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { expanded = !expanded }
                ) {
                    Text(
                        text = selected.stringResource()
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = expanded,
                    onCheckedChange = { expanded = it }
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f
                    )

                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        modifier =
                        Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                            this.rotationZ = rotation
                        },
                        contentDescription = null
                    )
                }

                // Had to put it here because it must be aligned to the trailing button
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    possibleValues.forEach {
                        DropdownMenuItem(
                            text = { Text(it.stringResource()) },
                            onClick = {
                                expanded = false
                                onSelect(it)
                            }
                        )
                    }
                }
            }
        )
    }
}
