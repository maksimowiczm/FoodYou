package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import kotlin.math.floor

enum class PortionType {
    Package,
    Serving;

    @Composable
    fun pluralStringResource(count: Int): String {
        return when (this) {
            Package -> pluralStringResource(R.plurals.product_package, count)
            Serving -> pluralStringResource(R.plurals.product_serving, count)
        }
    }
}

enum class InputError {
    Empty,
    Invalid,
    Negative,
    Zero
}

abstract class MutablePortion<T>(
    initialQuantity: T,
    val weightUnit: WeightUnit,
    private val calculateCalories: (T) -> Int
) {
    var quantity: T by mutableStateOf(initialQuantity)
        protected set

    val calories: Int by derivedStateOf { calculateCalories(quantity) }

    var error: InputError? by mutableStateOf(null)
        protected set

    abstract fun parse(input: String): Result<T, InputError>

    fun onWeightChange(newWeight: String) {
        val result = parse(newWeight)

        if (result.isOk) {
            quantity = result.value
            error = null
        } else {
            error = result.error
        }
    }
}

@Composable
fun rememberMutableWeightUnitPortion(
    initialWeight: Int,
    weightUnit: WeightUnit,
    calculateCalories: (Int) -> Int
): MutableWeightUnitPortion = rememberSaveable(
    saver = Saver(
        save = { portion -> portion.quantity },
        restore = {
            MutableWeightUnitPortion(
                initialWeight = it,
                weightUnit = weightUnit,
                calculateCalories = calculateCalories
            )
        }
    )
) {
    MutableWeightUnitPortion(
        initialWeight = initialWeight,
        weightUnit = weightUnit,
        calculateCalories = calculateCalories
    )
}

class MutableWeightUnitPortion(
    initialWeight: Int,
    weightUnit: WeightUnit,
    calculateCalories: (Int) -> Int
) : MutablePortion<Int>(
    initialQuantity = initialWeight,
    calculateCalories = calculateCalories,
    weightUnit = weightUnit
) {
    override fun parse(input: String): Result<Int, InputError> {
        if (input.isBlank()) {
            return Err(InputError.Empty)
        }

        val weight = input.toIntOrNull() ?: return Err(InputError.Invalid)

        if (weight < 0) {
            return Err(InputError.Negative)
        }

        if (weight == 0) {
            return Err(InputError.Zero)
        }

        return Ok(weight)
    }
}

@Composable
fun rememberMutableDefinedPortion(
    initialQuantity: Float,
    weightUnit: WeightUnit,
    calculateCalories: (Float) -> Int,
    calculateWeight: (Float) -> Int,
    portionType: PortionType
): MutableDefinedPortion = rememberSaveable(
    saver = Saver(
        save = { portion -> portion.quantity },
        restore = {
            MutableDefinedPortion(
                initialQuantity = it,
                weightUnit = weightUnit,
                calculateCalories = calculateCalories,
                calculateWeight = calculateWeight,
                portionType = portionType
            )
        }
    )
) {
    MutableDefinedPortion(
        initialQuantity = initialQuantity,
        weightUnit = weightUnit,
        calculateCalories = calculateCalories,
        calculateWeight = calculateWeight,
        portionType = portionType
    )
}

class MutableDefinedPortion(
    initialQuantity: Float,
    weightUnit: WeightUnit,
    calculateWeight: (Float) -> Int,
    calculateCalories: (Float) -> Int,
    private val portionType: PortionType
) : MutablePortion<Float>(
    initialQuantity = initialQuantity,
    weightUnit = weightUnit,
    calculateCalories = calculateCalories
) {
    val weight by derivedStateOf { calculateWeight(quantity) }

    // TODO this might be bad idea because languages have different pluralization rules for floats
    val label: String
        @Composable get() = portionType.pluralStringResource(floor(quantity).toInt())

    override fun parse(input: String): Result<Float, InputError> {
        if (input.isBlank()) {
            return Err(InputError.Empty)
        }

        val weight = input.toFloatOrNull() ?: return Err(InputError.Invalid)

        if (weight < 0) {
            return Err(InputError.Negative)
        }

        if (weight == 0f) {
            return Err(InputError.Zero)
        }

        return Ok(weight)
    }
}
