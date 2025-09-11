package com.maksimowiczm.foodyou.feature.food.product.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.shared.extension.now
import com.maksimowiczm.foodyou.feature.food.product.ui.ProductFormState
import com.maksimowiczm.foodyou.feature.food.product.ui.nutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.usecase.CreateProductUseCase
import com.maksimowiczm.foodyou.shared.common.FoodYouLogger
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
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
            FoodYouLogger.e(TAG) { "Form is not valid, cannot create product." }
            return
        }

        val multiplier =
            when (form.measurement) {
                is Measurement.ImmutableMeasurement -> 1f
                is Measurement.Package -> form.packageWeight.value?.let { 1 / it * 100 }
                is Measurement.Serving -> form.servingWeight.value?.let { 1 / it * 100 }
            }

        if (multiplier == null) {
            FoodYouLogger.e(TAG) { "Multiplier is null, cannot create product." }
            return
        }

        viewModelScope.launch {
            createProductUseCase
                .create(
                    name = form.name.value,
                    brand = form.brand.value,
                    barcode = form.barcode.value,
                    note = form.note.value,
                    isLiquid = form.isLiquid,
                    packageWeight = form.packageWeight.value?.toDouble(),
                    servingWeight = form.servingWeight.value?.toDouble(),
                    source = FoodSource(type = form.sourceType, url = form.sourceUrl.value),
                    nutritionFacts = form.nutritionFacts(multiplier),
                    history = FoodHistory.Created(LocalDateTime.now()),
                )
                .fold(
                    onSuccess = {
                        FoodYouLogger.i(TAG) { "Product created successfully with ID: $it" }
                        eventBus.send(CreateProductEvent.Created(it))
                    },
                    onFailure = { FoodYouLogger.e(TAG) { "Failed to create product" } },
                )
        }
    }

    private companion object {
        const val TAG = "CreateProductViewModel"
    }
}
