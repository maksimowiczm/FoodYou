package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.productredesign.ui.update.UpdateProductApp

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
