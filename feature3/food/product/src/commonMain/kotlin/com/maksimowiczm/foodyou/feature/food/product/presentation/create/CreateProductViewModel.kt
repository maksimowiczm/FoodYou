package com.maksimowiczm.foodyou.feature.food.product.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.CreateProductError
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.product.ui.ProductFormState
import com.maksimowiczm.foodyou.feature.food.product.ui.nutritionFacts
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class CreateProductViewModel(private val commandBus: CommandBus) : ViewModel() {
    private val eventBus = Channel<CreateProductEvent>()
    val events = eventBus.receiveAsFlow()

    fun createProduct(form: ProductFormState) {
        if (!form.isValid) {
            FoodYouLogger.e(TAG) { "Form is not valid, cannot create product." }
            return
        }

        val multiplier =
            when (form.measurement) {
                is Measurement.Gram -> 1f
                is Measurement.Milliliter -> 1f
                is Measurement.Package -> form.packageWeight.value?.let { 1 / it * 100 }
                is Measurement.Serving -> form.servingWeight.value?.let { 1 / it * 100 }
            }

        if (multiplier == null) {
            FoodYouLogger.e(TAG) { "Multiplier is null, cannot create product." }
            return
        }

        viewModelScope.launch {
            commandBus
                .dispatch<FoodId.Product, CreateProductError>(
                    CreateProductCommand(
                        name = form.name.value,
                        brand = form.brand.value,
                        barcode = form.barcode.value,
                        note = form.note.value,
                        isLiquid = form.isLiquid,
                        packageWeight = form.packageWeight.value?.toDouble(),
                        servingWeight = form.servingWeight.value?.toDouble(),
                        source = FoodSource(type = form.sourceType, url = form.sourceUrl.value),
                        nutritionFacts = form.nutritionFacts(multiplier),
                        event = FoodEvent.Created(LocalDateTime.Companion.now()),
                    )
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
