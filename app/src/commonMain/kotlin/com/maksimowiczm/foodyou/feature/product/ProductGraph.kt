package com.maksimowiczm.foodyou.feature.product

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

fun NavGraphBuilder.productGraph(
    createOnBack: () -> Unit,
    updateOnBack: () -> Unit,
    onCreateProduct: (FoodId.Product) -> Unit,
    onUpdateProduct: (FoodId.Product) -> Unit
) {
    forwardBackwardComposable<CreateProduct> {
        CreateProductScreen(
            onBack = createOnBack,
            onCreate = { id -> onCreateProduct(FoodId.Product(id)) }
        )
    }
    forwardBackwardComposable<UpdateProduct> {
        val (productId) = it.toRoute<UpdateProduct>()

        UpdateProductScreen(
            productId = productId,
            onBack = updateOnBack,
            onUpdate = { onUpdateProduct(FoodId.Product(productId)) }
        )
    }
}

@Serializable
data object CreateProduct

@Serializable
data class UpdateProduct(val productId: Long)
