package com.maksimowiczm.foodyou.feature.diary.ui.product

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.feature.diary.data.NutrientsHelper
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.allowNull
import com.maksimowiczm.foodyou.ui.form.between
import com.maksimowiczm.foodyou.ui.form.notNull
import com.maksimowiczm.foodyou.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.ui.form.positive
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

enum class ProductFormError {
    Required,
    NotANumber,
    NegativeNumber,
    Exceeds100
    ;

    @Composable
    fun stringResource() = when (this) {
        Required -> "* " + stringResource(Res.string.neutral_required)
        NotANumber -> stringResource(Res.string.error_invalid_number)
        NegativeNumber -> stringResource(Res.string.error_value_cannot_be_negative)
        Exceeds100 -> stringResource(Res.string.error_value_cannot_exceed_100)
    }
}

enum class GlobalError {
    MacronutrientsSumExceeds100;

    @Composable
    fun stringResource() = when (this) {
        MacronutrientsSumExceeds100 -> stringResource(
            Res.string.error_sum_of_macronutrients_cannot_exceed_100g
        )
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
        ) {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val carbohydrates = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.carbohydrates,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        notNull(
            onError = { ProductFormError.Required }
        ) {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val fats = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.fats,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        notNull(
            onError = { ProductFormError.Required }
        ) {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val calories = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.calories,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    )

    val sugars = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.sugars?.value,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val saturatedFats = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.saturatedFats?.value,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val salt = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.salt?.value,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val sodium = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.sodium?.value,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val fiber = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.fiber?.value,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            between(
                min = 0f,
                onMinError = { ProductFormError.NegativeNumber },
                max = 100f,
                onMaxError = { ProductFormError.Exceeds100 }
            )
        }
    }

    val packageWeight = rememberFormFieldWithTextFieldValue(
        initialValue = product?.packageWeight,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            positive(
                onError = { ProductFormError.NegativeNumber }
            )
        }
    }

    val servingWeight = rememberFormFieldWithTextFieldValue(
        initialValue = product?.servingWeight,
        parser = nullableFloatParser(
            onNan = { ProductFormError.NotANumber }
        ),
        formatter = {
            (it?.toString() ?: "").trimEnd('0').trimEnd('.')
        }
    ) {
        allowNull {
            positive(
                onError = { ProductFormError.NegativeNumber }
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        product,
        saver = Saver(
            save = {
                it.weightUnit
            },
            restore = {
                ProductFormState(
                    name = name,
                    brand = brand,
                    barcode = barcode,
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    calories = calories,
                    sugars = sugars,
                    saturatedFats = saturatedFats,
                    salt = salt,
                    sodium = sodium,
                    fiber = fiber,
                    packageWeight = packageWeight,
                    servingWeight = servingWeight,
                    initialWeightUnit = it,
                    coroutineScope = coroutineScope
                )
            }
        )
    ) {
        ProductFormState(
            name = name,
            brand = brand,
            barcode = barcode,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            calories = calories,
            sugars = sugars,
            saturatedFats = saturatedFats,
            salt = salt,
            sodium = sodium,
            fiber = fiber,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            initialWeightUnit = product?.weightUnit ?: WeightUnit.Gram,
            coroutineScope = coroutineScope
        )
    }
}

class ProductFormState(
    val name: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val brand: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val barcode: FormFieldWithTextFieldValue<String?, ProductFormError>,
    val proteins: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val carbohydrates: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val fats: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val calories: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val sugars: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val saturatedFats: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val salt: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val sodium: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val fiber: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val packageWeight: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    val servingWeight: FormFieldWithTextFieldValue<Float?, ProductFormError>,
    initialWeightUnit: WeightUnit,
    coroutineScope: CoroutineScope
) {
    var weightUnit by mutableStateOf(initialWeightUnit)

    val globalError by derivedStateOf {
        if (proteins.value != null && carbohydrates.value != null && fats.value != null) {
            val sum = proteins.value + carbohydrates.value + fats.value
            if (sum > 100) {
                return@derivedStateOf GlobalError.MacronutrientsSumExceeds100
            }
        }

        null
    }

    val isValid: Boolean by derivedStateOf {
        globalError == null &&
            name.isValid &&
            brand.isValid &&
            barcode.isValid &&
            proteins.isValid &&
            carbohydrates.isValid &&
            fats.isValid &&
            calories.isValid &&
            sugars.isValid &&
            saturatedFats.isValid &&
            salt.isValid &&
            sodium.isValid &&
            fiber.isValid &&
            packageWeight.isValid &&
            servingWeight.isValid
    }

    val isDirty by derivedStateOf {
        name.dirty ||
            brand.dirty ||
            barcode.dirty ||
            proteins.dirty ||
            carbohydrates.dirty ||
            fats.dirty ||
            // calories.dirty || // ignore calories because it's calculated
            sugars.dirty ||
            saturatedFats.dirty ||
            salt.dirty ||
            sodium.dirty ||
            fiber.dirty ||
            packageWeight.dirty ||
            servingWeight.dirty
    }

    init {
        coroutineScope.launch {
            combine(
                snapshotFlow { proteins.value }.filterNotNull(),
                snapshotFlow { carbohydrates.value }.filterNotNull(),
                snapshotFlow { fats.value }.filterNotNull()
            ) { proteins, carbohydrates, fats ->
                NutrientsHelper.calculateCalories(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats
                )
            }.collectLatest {
                calories.onRawValueChange(it)
            }
        }
    }
}
