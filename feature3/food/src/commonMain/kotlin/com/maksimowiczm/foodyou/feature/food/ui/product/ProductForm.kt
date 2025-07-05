package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.form.FormField
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
