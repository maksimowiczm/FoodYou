package com.maksimowiczm.foodyou.app.ui.userfood.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormState
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormState.Companion.optionalField
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormState.Companion.requiredField
import com.maksimowiczm.foodyou.app.ui.userfood.ProductFormTransformer
import com.maksimowiczm.foodyou.app.ui.userfood.QuantityUnit
import com.maksimowiczm.foodyou.app.ui.userfood.ValuesPer
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProductViewModel(
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val userFoodRepository: UserFoodRepository,
    private val productFormTransformer: ProductFormTransformer,
) : ViewModel() {

    private val eventBus = Channel<CreateProductEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    private val _productFormState = MutableStateFlow(ProductFormState())
    val productFormState = _productFormState.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    fun create() {
        val isLocked = _isLocked.value
        if (isLocked) return
        _isLocked.value = true

        val form = _productFormState.value.copy()
        require(form.isValid) { "Form is not valid" }

        viewModelScope.launch {
            val (
                foodName,
                brand,
                barcode,
                note,
                source,
                nutritionFacts,
                servingQuantity,
                packageQuantity,
                isLiquid,
            ) = productFormTransformer.validate(form)

            val accountId = observePrimaryAccountUseCase.observe().first().localAccountId

            val id =
                userFoodRepository.create(
                    name = foodName,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    imageUri = form.imageUri,
                    source = source,
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    accountId = accountId,
                    isLiquid = isLiquid,
                )

            eventBus.send(CreateProductEvent.Created(id))
        }
    }

    fun setImage(uri: String?) {
        _productFormState.value = _productFormState.value.copy(imageUri = uri)
    }

    fun setValuesPer(valuesPer: ValuesPer) {
        _productFormState.update {
            val servingQuantity =
                when (valuesPer) {
                    ValuesPer.Serving ->
                        requiredField(textFieldState = it.servingQuantity.textFieldState)

                    else -> optionalField(textFieldState = it.servingQuantity.textFieldState)
                }

            val packageQuantity =
                when (valuesPer) {
                    ValuesPer.Package ->
                        requiredField(textFieldState = it.packageQuantity.textFieldState)

                    else -> optionalField(textFieldState = it.packageQuantity.textFieldState)
                }

            it.copy(
                valuesPer = valuesPer,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )
        }
    }

    fun setServingUnit(unit: QuantityUnit) {
        _productFormState.update { it.copy(servingUnit = unit) }
    }

    fun setPackageUnit(unit: QuantityUnit) {
        _productFormState.update { it.copy(packageUnit = unit) }
    }
}
