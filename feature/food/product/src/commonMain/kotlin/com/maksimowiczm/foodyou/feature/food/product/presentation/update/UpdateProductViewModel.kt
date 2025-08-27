package com.maksimowiczm.foodyou.feature.food.product.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.ObserveFoodUseCase
import com.maksimowiczm.foodyou.business.food.application.UpdateProductUseCase
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.feature.food.product.ui.ProductFormState
import com.maksimowiczm.foodyou.feature.food.product.ui.nutritionFacts
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateProductViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val productId: FoodId.Product,
) : ViewModel() {

    val product =
        observeFoodUseCase
            .observe(productId)
            .mapNotNull { it as Product }
            .stateIn(scope = viewModelScope, initialValue = null, started = WhileSubscribed(2_000))

    private val eventBus = Channel<UpdateProductEvent>()
    val events = eventBus.receiveAsFlow()

    fun updateProduct(form: ProductFormState) {
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
            updateProductUseCase
                .update(
                    id = productId,
                    name = form.name.value,
                    brand = form.brand.value,
                    barcode = form.barcode.value,
                    nutritionFacts = form.nutritionFacts(multiplier),
                    packageWeight = form.packageWeight.value?.toDouble(),
                    servingWeight = form.servingWeight.value?.toDouble(),
                    note = form.note.value,
                    source = FoodSource(type = form.sourceType, url = form.sourceUrl.value),
                    isLiquid = form.isLiquid,
                )
                .consume(
                    onSuccess = { eventBus.send(UpdateProductEvent.Updated) },
                    onFailure = { FoodYouLogger.e(TAG) { "Failed to update product" } },
                )
        }
    }

    private companion object {
        const val TAG = "UpdateProductViewModel"
    }
}
