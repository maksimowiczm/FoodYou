package com.maksimowiczm.foodyou.app.ui.userfood.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormState
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormTransformer
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class CreateProductViewModel(
    private val appAccountManager: AppAccountManager,
    private val userProductRepository: UserProductRepository,
    private val productFormTransformer: ProductFormTransformer,
) : ViewModel() {

    val energyFormat =
        appAccountManager
            .observeAppAccount()
            .map { it.settings.energyFormat }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = EnergyFormat.Kilocalories,
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
                image,
                nutritionFacts,
                servingQuantity,
                packageQuantity,
                isLiquid,
            ) = productFormTransformer.transform(form)

            val accountId = appAccountManager.observeAppAccountId().filterNotNull().first()

            val id =
                userProductRepository.create(
                    name = foodName,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    image = image,
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    accountId = accountId,
                    isLiquid = isLiquid,
                )

            eventBus.send(CreateProductEvent.Created(id))
        }
    }
}
