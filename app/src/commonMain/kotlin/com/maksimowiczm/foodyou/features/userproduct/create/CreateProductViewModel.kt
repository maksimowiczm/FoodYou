package com.maksimowiczm.foodyou.features.userproduct.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.ObserveEnergyUnitUseCase
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.features.userproduct.ProductFormState
import com.maksimowiczm.foodyou.features.userproduct.ProductFormTransformer
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val userProductService: UserProductService,
    private val blobStorage: BlobStorage,
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

            val id = UserProductId()
            val product =
                UserProduct(
                    id = id,
                    name = foodName,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    image = imageBytes?.let { blobStorage.store(it) },
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    isLiquid = isLiquid,
                )

            userProductService.handle(
                id = id,
                command = UserProductCommand.Create(product = product),
            )

            eventBus.send(CreateProductEvent.Created(id))
        }
    }
}
