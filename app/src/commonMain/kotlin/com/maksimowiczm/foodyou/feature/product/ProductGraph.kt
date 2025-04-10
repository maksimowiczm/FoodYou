package com.maksimowiczm.foodyou.feature.product

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.fullScreenDialogComposable
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProduct
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProduct
import kotlinx.serialization.Serializable

@Serializable
data object CreateProduct

@Serializable
data class UpdateProduct(val productId: Long)

fun NavGraphBuilder.productGraph(
    onCreateProduct: (productId: Long) -> Unit,
    onCreateClose: () -> Unit,
    onUpdateProduct: (productId: Long) -> Unit,
    onUpdateClose: () -> Unit
) {
    fullScreenDialogComposable<CreateProduct> {
        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            CreateProduct(
                onCreate = onCreateProduct,
                onClose = onCreateClose
            )
        }
    }
    fullScreenDialogComposable<UpdateProduct> {
        val (id) = it.toRoute<UpdateProduct>()

        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            UpdateProduct(
                productId = id,
                onUpdate = onUpdateProduct,
                onClose = onUpdateClose
            )
        }
    }
}
