package com.maksimowiczm.foodyou.feature.food.ui.product.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.UpdateProductUseCase
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.food.ui.product.toProduct
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateProductViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val productId: FoodId.Product
) : ViewModel() {

    val product = observeFoodUseCase.observe(productId).stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = WhileSubscribed(2_000)
    )

    private val eventBus = Channel<UpdateProductEvent>()
    val events = eventBus.receiveAsFlow()

    fun updateProduct(form: ProductFormState) {
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

        val product = form.toProduct(multiplier, productId).getOrElse {
            Logger.w(TAG) { "Failed to convert form state to Product entity: ${it.message}" }
            return
        }

        viewModelScope.launch {
            runCatching {
                updateProductUseCase.update(product)
                eventBus.send(UpdateProductEvent.Updated)
            }
        }
    }

    private companion object {
        const val TAG = "UpdateProductViewModel"
    }
}
