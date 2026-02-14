package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.form.FormField2
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField2
import foodyou.app.generated.resources.*
import io.konform.validation.ifPresent

@Stable
internal class ProductForm2State(
    val name: FormField2,
    val brand: FormField2,
    val barcode: FormField2,
    private val defaultImageUri: String?,
    val imageUri: MutableState<String?>,
    private val defaultValuesPer: ValuesPer,
    val valuesPer: MutableState<ValuesPer>,
    val servingQuantity: FormField2,
    private val defaultServingUnit: QuantityUnit,
    val servingUnit: MutableState<QuantityUnit>,
    val packageQuantity: FormField2,
    private val defaultPackageUnit: QuantityUnit,
    val packageUnit: MutableState<QuantityUnit>,
    val note: FormField2,
) {
    val isModified: Boolean by derivedStateOf {
        name.isModified ||
            brand.isModified ||
            barcode.isModified ||
            imageUri.value != defaultImageUri ||
            valuesPer.value != defaultValuesPer ||
            servingQuantity.isModified ||
            servingUnit.value != defaultServingUnit ||
            packageQuantity.isModified ||
            packageUnit.value != defaultPackageUnit ||
            note.isModified
    }

    val isValid: Boolean by derivedStateOf {
        name.error == null &&
            name.textFieldState.text.isNotBlank() &&
            brand.error == null &&
            barcode.error == null &&
            servingQuantity.error == null &&
            packageQuantity.error == null &&
            note.error == null
    }

    val hasSuggestedFieldsFilled: Boolean by derivedStateOf { false }
}

@Composable
internal fun rememberProductForm2State(
    defaultImageUri: String? = null,
    defaultValuesPer: ValuesPer = ValuesPer.Grams100,
    defaultServingUnit: QuantityUnit = QuantityUnit.Gram,
    defaultPackageUnit: QuantityUnit = QuantityUnit.Gram,
): ProductForm2State {
    val name = rememberFormField2 {
        constrain(stringResource(Res.string.neutral_required)) { !it.isNullOrBlank() }
    }
    val brand = rememberFormField2()
    val barcode = rememberFormField2 {
        ifPresent {
            constrain(this@rememberFormField2.stringResource(Res.string.error_not_a_barcode)) {
                it.all(Char::isDigit)
            }
        }
    }
    val imageUri = remember(defaultImageUri) { mutableStateOf<String?>(defaultImageUri) }
    val valuesPer = remember { mutableStateOf(defaultValuesPer) }

    val servingQuantity =
        rememberFormField2(valuesPer.value) {
            if (valuesPer.value == ValuesPer.Serving) {
                constrain(this@rememberFormField2.stringResource(Res.string.neutral_required)) {
                    it != null
                }
            }

            ifPresent {
                constrain(this@rememberFormField2.stringResource(Res.string.error_invalid_number)) {
                    it.toDoubleOrNull() != null
                }
                constrain(
                    this@rememberFormField2.stringResource(Res.string.error_value_must_be_positive)
                ) {
                    it.toDoubleOrNull()?.let { it > 0 } ?: true
                }
            }
        }
    val servingUnit = remember(defaultServingUnit) { mutableStateOf(defaultServingUnit) }

    val packageQuantity =
        rememberFormField2(valuesPer.value) {
            if (valuesPer.value == ValuesPer.Package) {
                constrain(this@rememberFormField2.stringResource(Res.string.neutral_required)) {
                    it != null
                }
            }

            ifPresent {
                constrain(this@rememberFormField2.stringResource(Res.string.error_invalid_number)) {
                    it.toDoubleOrNull() != null
                }
                constrain(
                    this@rememberFormField2.stringResource(Res.string.error_value_must_be_positive)
                ) {
                    it.toDoubleOrNull()?.let { it > 0 } ?: true
                }
            }
        }
    val packageUnit = remember(defaultPackageUnit) { mutableStateOf(defaultPackageUnit) }

    val note = rememberFormField2()

    return remember(
        name,
        brand,
        barcode,
        defaultImageUri,
        imageUri,
        defaultValuesPer,
        valuesPer,
        servingQuantity,
        defaultServingUnit,
        servingUnit,
        packageQuantity,
        defaultPackageUnit,
        packageUnit,
        note,
    ) {
        ProductForm2State(
            name,
            brand,
            barcode,
            defaultImageUri,
            imageUri,
            defaultValuesPer,
            valuesPer,
            servingQuantity,
            defaultServingUnit,
            servingUnit,
            packageQuantity,
            defaultPackageUnit,
            packageUnit,
            note,
        )
    }
}
