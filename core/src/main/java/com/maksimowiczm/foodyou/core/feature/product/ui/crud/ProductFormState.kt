package com.maksimowiczm.foodyou.core.feature.product.ui.crud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.stringResource
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentHelper
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.core.ui.form.allowNull
import com.maksimowiczm.foodyou.core.ui.form.between
import com.maksimowiczm.foodyou.core.ui.form.nonNegative
import com.maksimowiczm.foodyou.core.ui.form.notEmpty
import com.maksimowiczm.foodyou.core.ui.form.notNull
import com.maksimowiczm.foodyou.core.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.core.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.core.ui.form.rememberFormFieldWithTextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Composable
fun rememberProductFormState(
    product: Product? = null
): ProductFormState {
    val name = rememberFormFieldWithTextFieldValue(
        initialValue = product?.name,
        requireDirty = product == null,
        parser = nullableStringParser()
    ) {
        notNull(
            onError = { MyError.Required }
        ) {
            notEmpty(
                onError = { MyError.Empty }
            )
        }
    }

    val brand = rememberFormFieldWithTextFieldValue(
        initialValue = product?.brand,
        requireDirty = product == null,
        parser = nullableStringParser<MyError>()
    )

    val barcode = rememberFormFieldWithTextFieldValue(
        initialValue = product?.barcode,
        requireDirty = product == null,
        parser = nullableStringParser<MyError>()
    )

    val calories = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.calories,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        // Actually, calories are always required, but these are automatically calculated from
        // proteins, carbohydrates, and fats so don't show an error
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val proteins = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.proteins,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        notNull(
            onError = { MyError.Required }
        ) {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val carbohydrates = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.carbohydrates,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        notNull(
            onError = { MyError.Required }
        ) {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val sugars = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.sugars,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val fats = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.fats,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        notNull(
            onError = { MyError.Required }
        ) {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val saturatedFats = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.saturatedFats,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val salt = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.salt,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val sodium = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.sodium,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val fiber = rememberFormFieldWithTextFieldValue(
        initialValue = product?.nutrients?.fiber,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            nonNegative(
                onError = { MyError.Negative }
            )
        }
    }

    val packageWeight = rememberFormFieldWithTextFieldValue(
        initialValue = product?.packageWeight,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            between(
                min = 0f,
                max = 100_000f,
                onMinError = { MyError.Negative },
                onMaxError = { MyError.NotANumber }
            )
        }
    }

    val servingWeight = rememberFormFieldWithTextFieldValue(
        initialValue = product?.servingWeight,
        requireDirty = product == null,
        parser = nullableFloatParser(
            onNan = { MyError.NotANumber }
        )
    ) {
        allowNull {
            between(
                min = 0f,
                max = 100_000f,
                onMinError = { MyError.Negative },
                onMaxError = { MyError.NotANumber }
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        saver = Saver(
            save = { state ->
                state.weightUnit
            },
            restore = {
                ProductFormState(
                    name = name,
                    brand = brand,
                    barcode = barcode,
                    calories = calories,
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    sugars = sugars,
                    fats = fats,
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
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            sugars = sugars,
            fats = fats,
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

enum class MyError {
    Required,
    Empty,
    NotANumber,
    Negative
    ;

    @Composable
    fun stringResource() = when (this) {
        Required -> stringResource(R.string.error_this_field_is_required)
        Empty -> stringResource(R.string.error_this_field_cannot_be_empty)
        NotANumber -> stringResource(R.string.error_invalid_number)
        Negative -> stringResource(R.string.error_value_cannot_be_negative)
    }
}

enum class GlobalError {
    MacronutrientsSumExceeds100;

    @Composable
    fun stringResource() = when (this) {
        MacronutrientsSumExceeds100 -> stringResource(R.string.error_sum_of_macronutrients_cannot_exceed_100g)
    }
}

@Stable
class ProductFormState(
    val name: FormFieldWithTextFieldValue<String?, MyError>,
    val brand: FormFieldWithTextFieldValue<String?, MyError>,
    val barcode: FormFieldWithTextFieldValue<String?, MyError>,
    val calories: FormFieldWithTextFieldValue<Float?, MyError>,
    val proteins: FormFieldWithTextFieldValue<Float?, MyError>,
    val carbohydrates: FormFieldWithTextFieldValue<Float?, MyError>,
    val sugars: FormFieldWithTextFieldValue<Float?, MyError>,
    val fats: FormFieldWithTextFieldValue<Float?, MyError>,
    val saturatedFats: FormFieldWithTextFieldValue<Float?, MyError>,
    val salt: FormFieldWithTextFieldValue<Float?, MyError>,
    val sodium: FormFieldWithTextFieldValue<Float?, MyError>,
    val fiber: FormFieldWithTextFieldValue<Float?, MyError>,
    val packageWeight: FormFieldWithTextFieldValue<Float?, MyError>,
    val servingWeight: FormFieldWithTextFieldValue<Float?, MyError>,
    initialWeightUnit: WeightUnit,
    coroutineScope: CoroutineScope
) {
    private val _all = listOf(
        name,
        brand,
        barcode,
        calories,
        proteins,
        carbohydrates,
        sugars,
        fats,
        saturatedFats,
        salt,
        sodium,
        fiber,
        packageWeight,
        servingWeight
    )

    init {
        coroutineScope.launch {
            combine(
                snapshotFlow { proteins.value },
                snapshotFlow { carbohydrates.value },
                snapshotFlow { fats.value }
            ) { proteins, carbohydrates, fats ->
                arrayListOf(proteins, carbohydrates, fats)
            }.collectLatest { (proteins, carbohydrates, fats) ->
                if (proteins == null || carbohydrates == null || fats == null) {
                    return@collectLatest
                }

                val caloriesValue = NutrimentHelper.calculateCalories(proteins, carbohydrates, fats)

                calories.onRawValueChange(caloriesValue)
            }
        }
    }

    fun validate() {
        _all.forEach { it.touch() }
    }

    var weightUnit by mutableStateOf(initialWeightUnit)

    val isValid: Boolean by derivedStateOf {
        _all.all { it.isValid } && globalError == null
    }

    val globalError by derivedStateOf {
        if (proteins.value != null && carbohydrates.value != null && fats.value != null) {
            val sum = proteins.value + carbohydrates.value + fats.value
            if (sum > 100) {
                return@derivedStateOf GlobalError.MacronutrientsSumExceeds100
            }
        }

        null
    }
}
