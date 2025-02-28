package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.brand

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.notNull
import com.maksimowiczm.foodyou.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

enum class ProductFormError {
    Required,
    NotANumber
    ;

    @Composable
    fun stringResource() = when (this) {
        Required -> stringResource(R.string.neutral_required)
        NotANumber -> stringResource(R.string.error_invalid_number)
    }
}

@Composable
fun rememberProductFormState(product: Product?): ProductFormState {
    val name = rememberFormFieldWithTextFieldValue(
        initialValue = product?.name,
        parser = nullableStringParser()
    ) {
        notNull(
            onError = { ProductFormError.Required }
        )
    }

    val brand = rememberFormFieldWithTextFieldValue(
        initialValue = product?.brand,
        parser = nullableStringParser<ProductFormError>()
    )

    val barcode = rememberFormFieldWithTextFieldValue(
        initialValue = product?.barcode,
        parser = nullableStringParser<ProductFormError>()
    )

    val proteins = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.proteins,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        notNull(
            onError = { ProductFormError.Required }
        )
    }

    return remember {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            proteins = proteins
        )
    }
}

class ProductFormState(
    val name: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val brand: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val barcode: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val proteins: FormFieldWithTextFieldValue<Float?, ProductFormError>
)

@Composable
fun <T> FormFieldWithTextFieldValue<T, ProductFormError>.TextField(
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
fun <T : Number?> FormFieldWithTextFieldValue<T, ProductFormError>.TextFieldNumber(
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
fun FormFieldWithTextFieldValue<String?, ProductFormError>.TextFieldString(
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
private fun requiredString(): String = "* " + stringResource(R.string.neutral_required)

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
