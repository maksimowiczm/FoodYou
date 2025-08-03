package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.food.ui.product.nutritionFacts
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class CreateProductViewModel(private val createProductUseCase: CreateProductUseCase) :
    ViewModel() {
    private val eventBus = Channel<CreateProductEvent>()
    val events = eventBus.receiveAsFlow()

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

        viewModelScope.launch {
            val id = createProductUseCase.create(
                name = form.name.value,
                brand = form.brand.value,
                barcode = form.barcode.value,
                nutritionFacts = form.nutritionFacts(multiplier),
                packageWeight = form.packageWeight.value,
                servingWeight = form.servingWeight.value,
                note = form.note.value,
                source = FoodSource(
                    type = form.sourceType,
                    url = form.sourceUrl.value
                ),
                isLiquid = form.isLiquid,
                event = FoodEvent.Created(LocalDateTime.now())
            )
            eventBus.send(CreateProductEvent.Created(id))
        }
    }

    private companion object {
        const val TAG = "CreateProductViewModel"
    }
}
