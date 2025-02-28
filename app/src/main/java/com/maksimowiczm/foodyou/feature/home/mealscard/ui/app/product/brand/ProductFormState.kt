package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.brand

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.notNull
import com.maksimowiczm.foodyou.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue

enum class ProductFormError {
    Required,
    NotANumber
    ;

    @Composable
    fun stringResource() = when (this) {
        Required -> androidx.compose.ui.res.stringResource(R.string.neutral_required)
        NotANumber -> androidx.compose.ui.res.stringResource(R.string.error_invalid_number)
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
