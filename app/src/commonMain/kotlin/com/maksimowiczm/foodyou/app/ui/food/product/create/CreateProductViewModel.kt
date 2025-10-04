package com.maksimowiczm.foodyou.app.ui.food.product.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.food.product.ProductFormState
import com.maksimowiczm.foodyou.app.ui.food.product.nutritionFacts
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.common.result.onError
import com.maksimowiczm.foodyou.common.result.onSuccess
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.usecase.CreateProductUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val dateProvider: DateProvider,
) : ViewModel() {
    private val eventBus = Channel<CreateProductEvent>()
    val events = eventBus.receiveAsFlow()

    fun createProduct(form: ProductFormState) {
        if (!form.isValid) {
            return
        }

        val multiplier =
            when (form.measurement) {
                is Measurement.ImmutableMeasurement -> 1f
                is Measurement.Package -> form.packageWeight.value?.let { 1 / it * 100 }
                is Measurement.Serving -> form.servingWeight.value?.let { 1 / it * 100 }
            }

        if (multiplier == null) {
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
                    history = FoodHistory.Created(dateProvider.nowInstant()),
                )
                .onSuccess { eventBus.send(CreateProductEvent.Created(it)) }
                .onError {
                    // Explode
                    error("Failed to create product: $it")
                }
        }
    }
}
