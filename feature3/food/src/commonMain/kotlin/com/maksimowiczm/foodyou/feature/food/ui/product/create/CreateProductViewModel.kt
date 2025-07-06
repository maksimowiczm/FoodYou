package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.food.ui.product.toProductEntity
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class CreateProductViewModel(foodDatabase: FoodDatabase) : ViewModel() {
    private val productDao = foodDatabase.productDao

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

        val entity = form.toProductEntity(multiplier).getOrElse {
            Logger.w(TAG) { "Failed to convert form state to Product entity: ${it.message}" }
            return
        }

        viewModelScope.launch {
            val id = productDao.insert(entity)
            eventBus.send(CreateProductEvent.Created(FoodId.Product(id)))
        }
    }

    private companion object {
        const val TAG = "CreateProductViewModel"
    }
}
