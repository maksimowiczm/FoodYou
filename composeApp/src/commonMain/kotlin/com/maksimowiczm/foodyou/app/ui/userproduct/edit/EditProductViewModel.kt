package com.maksimowiczm.foodyou.app.ui.userproduct.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.userproduct.ProductFormState
import com.maksimowiczm.foodyou.app.ui.userproduct.ProductFormTransformer
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
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
    private val productFormTransformer: ProductFormTransformer,
    private val identity: UserProductIdentity,
) : ViewModel() {
    private val eventBus = Channel<EditProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    val isLocked: StateFlow<Boolean>
        field = MutableStateFlow(true)

    val product =
        userProductService
            .observe(identity)
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
                image,
                nutritionFacts,
                servingQuantity,
                packageQuantity,
                isLiquid) =
                productFormTransformer.transform(form)

            userProductService.edit(
                identity = identity,
                name = foodName,
                brand = brand,
                barcode = barcode,
                note = note,
                imageBytes = image,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )

            eventBus.send(EditProductEvent.Edited)
        }
    }
}
