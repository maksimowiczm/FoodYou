package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.product.domain.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class CreateProductScreenViewModel(private val productRepository: ProductRepository) :
    ViewModel() {

    private val eventBus = MutableStateFlow<CreateProductEvent?>(null)
    val events = eventBus.filterNotNull()

    fun onCreate(productForm: ProductFormState) = launch {
        if (!productForm.isValid) {
            return@launch
        }

        val multiplier = when (productForm.measurement) {
            is Measurement.Gram -> 1f
            is Measurement.Package -> productForm.packageWeight.value?.let { 1 / it * 100 }
            is Measurement.Serving -> productForm.servingWeight.value?.let { 1 / it * 100 }
        } ?: return@launch

        val id = productRepository.createProduct(
            name = productForm.name.value,
            brand = productForm.brand.value,
            barcode = productForm.barcode.value,
            proteins = productForm.proteins.value?.let { it * multiplier } ?: return@launch,
            carbohydrates = productForm.carbohydrates.value
                ?.let { it * multiplier } ?: return@launch,
            fats = productForm.fats.value?.let { it * multiplier } ?: return@launch,
            calories = productForm.calories.value?.let { it * multiplier } ?: return@launch,
            saturatedFats = productForm.saturatedFats.value?.let { it * multiplier },
            monounsaturatedFats = productForm.monounsaturatedFats.value?.let { it * multiplier },
            polyunsaturatedFats = productForm.polyunsaturatedFats.value?.let { it * multiplier },
            omega3 = productForm.omega3.value?.let { it * multiplier },
            omega6 = productForm.omega6.value?.let { it * multiplier },
            sugars = productForm.sugars.value?.let { it * multiplier },
            salt = productForm.salt.value?.let { it * multiplier },
            fiber = productForm.fiber.value?.let { it * multiplier },
            cholesterolMilli = productForm.cholesterolMilli.value?.let { it * multiplier },
            caffeineMilli = productForm.caffeineMilli.value?.let { it * multiplier },
            vitaminAMicro = productForm.vitaminAMicro.value?.let { it * multiplier },
            vitaminB1Milli = productForm.vitaminB1Milli.value?.let { it * multiplier },
            vitaminB2Milli = productForm.vitaminB2Milli.value?.let { it * multiplier },
            vitaminB3Milli = productForm.vitaminB3Milli.value?.let { it * multiplier },
            vitaminB5Milli = productForm.vitaminB5Milli.value?.let { it * multiplier },
            vitaminB6Milli = productForm.vitaminB6Milli.value?.let { it * multiplier },
            vitaminB7Micro = productForm.vitaminB7Micro.value?.let { it * multiplier },
            vitaminB9Micro = productForm.vitaminB9Micro.value?.let { it * multiplier },
            vitaminB12Micro = productForm.vitaminB12Micro.value?.let { it * multiplier },
            vitaminCMilli = productForm.vitaminCMilli.value?.let { it * multiplier },
            vitaminDMicro = productForm.vitaminDMicro.value?.let { it * multiplier },
            vitaminEMilli = productForm.vitaminEMilli.value?.let { it * multiplier },
            vitaminKMicro = productForm.vitaminKMicro.value?.let { it * multiplier },
            manganeseMilli = productForm.manganeseMilli.value?.let { it * multiplier },
            magnesiumMilli = productForm.magnesiumMilli.value?.let { it * multiplier },
            potassiumMilli = productForm.potassiumMilli.value?.let { it * multiplier },
            calciumMilli = productForm.calciumMilli.value?.let { it * multiplier },
            copperMilli = productForm.copperMilli.value?.let { it * multiplier },
            zincMilli = productForm.zincMilli.value?.let { it * multiplier },
            seleniumMicro = productForm.seleniumMicro.value?.let { it * multiplier },
            iodineMicro = productForm.iodineMicro.value?.let { it * multiplier },
            sodiumMilli = productForm.sodiumMilli.value?.let { it * multiplier },
            ironMilli = productForm.ironMilli.value?.let { it * multiplier },
            phosphorusMilli = productForm.phosphorusMilli.value?.let { it * multiplier },
            chromiumMicro = productForm.chromiumMicro.value?.let { it * multiplier },
            packageWeight = productForm.packageWeight.value,
            servingWeight = productForm.servingWeight.value,
            isLiquid = TODO()
        )

        eventBus.emit(CreateProductEvent.Created(id))
    }
}
