package com.maksimowiczm.foodyou.feature.food.ui.product.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.food.ui.product.toProductEntity
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateProductScreenViewModel(
    foodDatabase: FoodDatabase,
    productMapper: ProductMapper,
    private val productId: FoodId.Product
) : ViewModel() {

    private val productDao = foodDatabase.productDao

    val product = productDao
        .observe(productId.id)
        .filterNotNull()
        .map(productMapper::toModel)
        .stateIn(
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

        val entity = form.toProductEntity(multiplier).getOrElse {
            Logger.w(TAG) { "Failed to convert form state to Product entity: ${it.message}" }
            return
        }.copy(
            id = productId.id
        )

        viewModelScope.launch {
            productDao.update(entity)
            eventBus.send(UpdateProductEvent.Updated)
        }
    }

    private companion object {
        const val TAG = "UpdateProductScreenViewModel"
    }
}
