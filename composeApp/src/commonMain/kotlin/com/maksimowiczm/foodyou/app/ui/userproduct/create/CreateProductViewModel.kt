package com.maksimowiczm.foodyou.app.ui.userproduct.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.userproduct.ObserveEnergyUnitUseCase
import com.maksimowiczm.foodyou.app.ui.userproduct.ProductFormState
import com.maksimowiczm.foodyou.app.ui.userproduct.ProductFormTransformer
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val userProductService: UserProductService,
    private val productFormTransformer: ProductFormTransformer,
    observeEnergyUnitUseCase: ObserveEnergyUnitUseCase,
) : ViewModel() {

    val energyFormat =
        observeEnergyUnitUseCase
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyUnit.Kilocalories,
            )

    private val eventBus = Channel<CreateProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun create(form: ProductFormState) {
        if (!isLocked.compareAndSet(expect = false, update = true)) return

        viewModelScope.launch {
            val (
                foodName,
                brand,
                barcode,
                note,
                imageBytes,
                nutritionFacts,
                servingQuantity,
                packageQuantity,
                isLiquid,
            ) = productFormTransformer.transform(form)

            val id =
                userProductService.create(
                    name = foodName,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    imageBytes = imageBytes,
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    isLiquid = isLiquid,
                )

            eventBus.send(CreateProductEvent.Created(id))
        }
    }
}
