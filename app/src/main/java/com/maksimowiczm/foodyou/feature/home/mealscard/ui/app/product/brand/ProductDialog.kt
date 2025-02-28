package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.brand

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun ProductForm(product: Product?, modifier: Modifier = Modifier) {
    val state = rememberProductFormState(product = product)

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            item {
                state.name.TextFieldString(
                    label = { Text(stringResource(R.string.product_name)) },
                    supportingText = { Text(requiredString()) }
                )
            }

            item {
                state.brand.TextFieldString(
                    label = { Text(stringResource(R.string.product_brand)) }
                )
            }

            item {
                state.barcode.TextFieldString(
                    label = { Text(stringResource(R.string.product_barcode)) }
                )
            }

            item {
                state.proteins.TextFieldNumber(
                    label = { Text(stringResource(R.string.nutriment_proteins)) },
                    supportingText = { Text(requiredString()) }
                )
            }
        }
    }
}

@Composable
private fun requiredString(): String = "* " + stringResource(R.string.neutral_required)

@Composable
private fun <T> FormFieldWithTextFieldValue<T, ProductFormError>.TextField(
    label: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    supportingText: @Composable (() -> Unit)? = null
) {
    TextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        label = label,
        isError = error != null,
        supportingText = {
            if (error == null) {
                supportingText?.invoke()
            } else {
                Text(error.stringResource())
            }
        },
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun <T : Number?> FormFieldWithTextFieldValue<T, ProductFormError>.TextFieldNumber(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: @Composable (() -> Unit)? = null
) {
    this.TextField(
        label = label,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        modifier = modifier,
        supportingText = supportingText
    )
}

@Composable
private fun FormFieldWithTextFieldValue<String?, ProductFormError>.TextFieldString(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: @Composable (() -> Unit)? = null
) {
    this.TextField(
        label = label,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        modifier = modifier,
        supportingText = supportingText
    )
}

@Preview
@Composable
private fun NullProductFormPreview() {
    FoodYouTheme {
        ProductForm(
            product = null
        )
    }
}

@Preview
@Composable
private fun ProductFormPreview() {
    FoodYouTheme {
        ProductForm(
            product = ProductPreviewParameterProvider().values.first()
        )
    }
}
