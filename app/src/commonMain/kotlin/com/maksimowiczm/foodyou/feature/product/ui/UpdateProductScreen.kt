package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductApp

@Composable
fun UpdateProductScreen(
    productId: Long,
    onBack: () -> Unit,
    onProductUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    UpdateProductApp(
        productId = productId,
        onBack = onBack,
        onProductUpdate = onProductUpdate,
        modifier = modifier
    )
}
