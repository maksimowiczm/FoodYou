package com.maksimowiczm.foodyou.feature.diary.ui.product

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.ui.product.create.CreateProductDialog
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductDialog
import com.maksimowiczm.foodyou.navigation.fullScreenDialogComposable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object CreateProduct

@Serializable
data class EditProduct(val productId: Long)

fun NavGraphBuilder.productGraph(
    onCreateClose: () -> Unit,
    onCreateSuccess: (productId: Long) -> Unit,
    onEditClose: () -> Unit,
    onEditSuccess: () -> Unit
) {
    fullScreenDialogComposable<CreateProduct> {
        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            CreateProductDialog(
                onClose = onCreateClose,
                onSuccess = onCreateSuccess
            )
        }
    }
    fullScreenDialogComposable<EditProduct> {
        val (productId) = it.toRoute<EditProduct>()

        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            UpdateProductDialog(
                onClose = onEditClose,
                onSuccess = onEditSuccess,
                viewModel = koinViewModel(
                    parameters = { parametersOf(productId) }
                )
            )
        }
    }
}
