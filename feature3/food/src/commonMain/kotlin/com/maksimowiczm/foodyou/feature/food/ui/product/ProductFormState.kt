package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

@Stable
internal class ProductFormState(
    // General
    val name: FormField<String, ProductFormFieldError>,
    val brand: FormField<String, ProductFormFieldError>,
    val barcode: FormField<String, ProductFormFieldError>,
    // Weight
    private val isLiquidState: MutableState<Boolean>,
    private val measurementState: MutableState<Measurement?>,
    val packageWeight: FormField<Float?, ProductFormFieldError>,
    val servingWeight: FormField<Float?, ProductFormFieldError>,
    // Macronutrients
    val proteins: FormField<Float?, ProductFormFieldError>,
    val carbohydrates: FormField<Float?, ProductFormFieldError>,
    val fats: FormField<Float?, ProductFormFieldError>,
    val calories: FormField<Float?, ProductFormFieldError>,
    // Fats
    val saturatedFats: FormField<Float?, ProductFormFieldError>,
    val monounsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val polyunsaturatedFats: FormField<Float?, ProductFormFieldError>,
    val omega3: FormField<Float?, ProductFormFieldError>,
    val omega6: FormField<Float?, ProductFormFieldError>,
    // Other
    val sugars: FormField<Float?, ProductFormFieldError>,
    val salt: FormField<Float?, ProductFormFieldError>,
    val fiber: FormField<Float?, ProductFormFieldError>,
    val cholesterolMilli: FormField<Float?, ProductFormFieldError>,
    val caffeineMilli: FormField<Float?, ProductFormFieldError>,
    // Vitamins
    val vitaminAMicro: FormField<Float?, ProductFormFieldError>,
    val vitaminB1Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB2Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB3Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB5Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB6Milli: FormField<Float?, ProductFormFieldError>,
    val vitaminB7Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminB9Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminB12Micro: FormField<Float?, ProductFormFieldError>,
    val vitaminCMilli: FormField<Float?, ProductFormFieldError>,
    val vitaminDMicro: FormField<Float?, ProductFormFieldError>,
    val vitaminEMilli: FormField<Float?, ProductFormFieldError>,
    val vitaminKMicro: FormField<Float?, ProductFormFieldError>,
    // Minerals
    val manganeseMilli: FormField<Float?, ProductFormFieldError>,
    val magnesiumMilli: FormField<Float?, ProductFormFieldError>,
    val potassiumMilli: FormField<Float?, ProductFormFieldError>,
    val calciumMilli: FormField<Float?, ProductFormFieldError>,
    val copperMilli: FormField<Float?, ProductFormFieldError>,
    val zincMilli: FormField<Float?, ProductFormFieldError>,
    val sodiumMilli: FormField<Float?, ProductFormFieldError>,
    val ironMilli: FormField<Float?, ProductFormFieldError>,
    val phosphorusMilli: FormField<Float?, ProductFormFieldError>,
    val chromiumMicro: FormField<Float?, ProductFormFieldError>,
    val seleniumMicro: FormField<Float?, ProductFormFieldError>,
    val iodineMicro: FormField<Float?, ProductFormFieldError>,
    // Extra
    val note: FormField<String, ProductFormFieldError>,
    isModifiedState: State<Boolean>
) {
    val isValid: Boolean
        get() = name.error == null &&
            brand.error == null &&
            barcode.error == null &&
            measurementState.value != null &&
            packageWeight.error == null &&
            servingWeight.error == null &&
            proteins.error == null &&
            carbohydrates.error == null &&
            fats.error == null &&
            calories.error == null &&
            saturatedFats.error == null &&
            monounsaturatedFats.error == null &&
            polyunsaturatedFats.error == null &&
            omega3.error == null &&
            omega6.error == null &&
            sugars.error == null &&
            salt.error == null &&
            fiber.error == null &&
            cholesterolMilli.error == null &&
            caffeineMilli.error == null &&
            vitaminAMicro.error == null &&
            vitaminB1Milli.error == null &&
            vitaminB2Milli.error == null &&
            vitaminB3Milli.error == null &&
            vitaminB5Milli.error == null &&
            vitaminB6Milli.error == null &&
            vitaminB7Micro.error == null &&
            vitaminB9Micro.error == null &&
            vitaminB12Micro.error == null &&
            vitaminCMilli.error == null &&
            vitaminDMicro.error == null &&
            vitaminEMilli.error == null &&
            vitaminKMicro.error == null &&
            manganeseMilli.error == null &&
            magnesiumMilli.error == null &&
            potassiumMilli.error == null &&
            calciumMilli.error == null &&
            copperMilli.error == null &&
            zincMilli.error == null &&
            sodiumMilli.error == null &&
            ironMilli.error == null &&
            phosphorusMilli.error == null &&
            seleniumMicro.error == null &&
            iodineMicro.error == null

    var isLiquid: Boolean by isLiquidState

    var measurement: Measurement
        get() = measurementState.value!!
        set(value) {
            measurementState.value = value
        }

    val isModified: Boolean by isModifiedState
}
