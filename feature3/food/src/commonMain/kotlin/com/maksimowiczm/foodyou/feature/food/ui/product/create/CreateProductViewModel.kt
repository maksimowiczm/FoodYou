package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.Minerals
import com.maksimowiczm.foodyou.feature.food.data.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.Product
import com.maksimowiczm.foodyou.feature.food.data.Vitamins
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class CreateProductViewModel(foodDatabase: FoodDatabase) : ViewModel() {
    private val productDao = foodDatabase.productDao

    private val _eventBus = Channel<CreateProductEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    fun createProduct(form: ProductFormState) {
        if (!form.isValid) {
            Logger.w(TAG) { "Form is not valid, cannot create product." }
            return
        }

        val multiplier = when (form.measurement) {
            is Measurement.Gram -> 1f
            is Measurement.Milliliter -> 1f
            is Measurement.Package -> form.packageWeight.value?.let { 1 / it * 100 }
            is Measurement.Serving -> form.servingWeight.value?.let { 1 / it * 100 }
        }

        if (multiplier == null) {
            Logger.w(TAG) { "Multiplier is null, cannot create product." }
            return
        }

        val entity = form.toProductEntity(multiplier).getOrElse {
            Logger.w(TAG) { "Failed to convert form state to Product entity: ${it.message}" }
            return
        }

        viewModelScope.launch {
            val id = productDao.insert(entity)
            _eventBus.send(CreateProductEvent.Created(FoodId.Product(id)))
        }
    }

    private companion object {
        const val TAG = "CreateProductViewModel"
    }
}

private fun ProductFormState.toProductEntity(multiplier: Float): Result<Product> = runCatching {
    val proteins = proteins.value
    checkNotNull(proteins) { "Proteins cannot be null" }

    val carbohydrates = carbohydrates.value
    checkNotNull(carbohydrates) { "Carbohydrates cannot be null" }

    val fats = fats.value
    checkNotNull(fats) { "Fats cannot be null" }

    val energy = energy.value
    checkNotNull(energy) { "Energy cannot be null" }

    return Result.success(
        Product(
            name = name.value,
            brand = brand.value,
            barcode = barcode.value,
            nutrients = Nutrients(
                proteins = proteins * multiplier,
                carbohydrates = carbohydrates * multiplier,
                fats = fats * multiplier,
                energy = energy * multiplier,
                saturatedFats = saturatedFats.value.normalize(multiplier),
                monounsaturatedFats = monounsaturatedFats.value.normalize(multiplier),
                polyunsaturatedFats = polyunsaturatedFats.value.normalize(multiplier),
                omega3 = omega3.value.normalize(multiplier),
                omega6 = omega6.value.normalize(multiplier),
                sugars = sugars.value.normalize(multiplier),
                salt = salt.value.normalize(multiplier),
                fiber = fiber.value.normalize(multiplier),
                cholesterolMilli = cholesterolMilli.value.normalize(multiplier),
                caffeineMilli = caffeineMilli.value.normalize(multiplier)
            ),
            vitamins = Vitamins(
                vitaminAMicro = vitaminAMicro.value.normalize(multiplier),
                vitaminB1Milli = vitaminB1Milli.value.normalize(multiplier),
                vitaminB2Milli = vitaminB2Milli.value.normalize(multiplier),
                vitaminB3Milli = vitaminB3Milli.value.normalize(multiplier),
                vitaminB5Milli = vitaminB5Milli.value.normalize(multiplier),
                vitaminB6Milli = vitaminB6Milli.value.normalize(multiplier),
                vitaminB7Micro = vitaminB7Micro.value.normalize(multiplier),
                vitaminB9Micro = vitaminB9Micro.value.normalize(multiplier),
                vitaminB12Micro = vitaminB12Micro.value.normalize(multiplier),
                vitaminCMilli = vitaminCMilli.value.normalize(multiplier),
                vitaminDMicro = vitaminDMicro.value.normalize(multiplier),
                vitaminEMilli = vitaminEMilli.value.normalize(multiplier),
                vitaminKMicro = vitaminKMicro.value.normalize(multiplier)
            ),
            minerals = Minerals(
                manganeseMilli = manganeseMilli.value.normalize(multiplier),
                magnesiumMilli = magnesiumMilli.value.normalize(multiplier),
                potassiumMilli = potassiumMilli.value.normalize(multiplier),
                calciumMilli = calciumMilli.value.normalize(multiplier),
                copperMilli = copperMilli.value.normalize(multiplier),
                zincMilli = zincMilli.value.normalize(multiplier),
                sodiumMilli = sodiumMilli.value.normalize(multiplier),
                ironMilli = ironMilli.value.normalize(multiplier),
                phosphorusMilli = phosphorusMilli.value.normalize(multiplier),
                seleniumMicro = seleniumMicro.value.normalize(multiplier),
                iodineMicro = iodineMicro.value.normalize(multiplier),
                chromiumMicro = chromiumMicro.value.normalize(multiplier)

            ),
            packageWeight = packageWeight.value,
            servingWeight = servingWeight.value,
            isLiquid = false, // TODO
            note = note.value
        )
    )
}

private fun Float?.normalize(multiplier: Float): Float? = this?.let { it * multiplier }
