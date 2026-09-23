package com.maksimowiczm.foodyou.features.userproduct.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.features.userproduct.ProductFormState
import com.maksimowiczm.foodyou.features.userproduct.ProductFormTransformer
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class EditProductViewModel(
    private val userProductService: UserProductService,
    private val blobStorage: BlobStorage,
    private val productFormTransformer: ProductFormTransformer,
    private val id: UserProductId,
) : ViewModel() {
    private val eventBus = Channel<EditProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(true)

    val product =
        userProductService
            .observe(id)
            .onEach { isLocked.value = false }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun save(form: ProductFormState) {
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
                isLiquid) =
                productFormTransformer.transform(form)

            val image = imageBytes?.let { bytes -> blobStorage.store(bytes) }
            userProductService.handle(
                id = id,
                command =
                    UserProductCommand.Update { product ->
                        product.copy(
                            name = foodName,
                            brand = brand,
                            barcode = barcode,
                            note = note,
                            image = image,
                            nutritionFacts = nutritionFacts,
                            servingQuantity = servingQuantity,
                            packageQuantity = packageQuantity,
                            isLiquid = isLiquid,
                        )
                    },
            )

            eventBus.send(EditProductEvent.Edited)
        }
    }
}
