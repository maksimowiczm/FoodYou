package com.maksimowiczm.foodyou.core.feature.product.ui.create

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
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentsAsGrams
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.core.ui.form.allowNull
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
fun rememberProductFormState(): ProductFormState {
    val name = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
        parser = nullableStringParser<MyError>()
    )

    val barcode = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
        parser = nullableStringParser<MyError>()
    )

    val calories = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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

    val proteins = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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

    val servingWeight = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = null,
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

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        saver = Saver(
            save = { state ->
                arrayListOf(
                    state.weightUnit,
                    state.autoCalculateCalories
                )
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
                    initialWeightUnit = it[0] as WeightUnit,
                    initialAutoCalculateCalories = it[1] as Boolean,
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
    initialWeightUnit: WeightUnit = WeightUnit.Gram,
    initialAutoCalculateCalories: Boolean = true,
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

    var autoCalculateCalories by mutableStateOf(initialAutoCalculateCalories)

    init {
        coroutineScope.launch {
            val proteinsFlow = combine(
                snapshotFlow { proteins.dirty },
                snapshotFlow { proteins.value }
            ) { dirty, value ->
                if (!dirty) {
                    null
                } else {
                    value
                }
            }

            val carbohydratesFlow = combine(
                snapshotFlow { carbohydrates.dirty },
                snapshotFlow { carbohydrates.value }
            ) { dirty, value ->
                if (!dirty) {
                    null
                } else {
                    value
                }
            }

            val fatsFlow = combine(
                snapshotFlow { fats.dirty },
                snapshotFlow { fats.value }
            ) { dirty, value ->
                if (!dirty) {
                    null
                } else {
                    value
                }
            }

            combine(
                snapshotFlow { autoCalculateCalories },
                proteinsFlow,
                carbohydratesFlow,
                fatsFlow
            ) { _, proteins, carbohydrates, fats ->
                arrayListOf(proteins, carbohydrates, fats)
            }.collectLatest { (proteins, carbohydrates, fats) ->
                if (!autoCalculateCalories) return@collectLatest

                if (proteins != null && carbohydrates != null && fats != null) {
                    val caloriesValue = NutrimentsAsGrams.calculateCalories(
                        proteins,
                        carbohydrates,
                        fats
                    )
                    val caloriesString = caloriesValue.toString().trimEnd('0').trimEnd('.')
                    calories.onValueChange(TextFieldValue(caloriesString), touch = false)
                }
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
