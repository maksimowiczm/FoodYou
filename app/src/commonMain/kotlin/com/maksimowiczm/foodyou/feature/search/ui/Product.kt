package com.maksimowiczm.foodyou.feature.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.search.domain.model.Product
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Product(
    productId: Long,
    viewModel: ProductViewModel = koinViewModel(),
    content: @Composable (Product?) -> Unit
) {
    val product by viewModel.observeProduct(productId).collectAsStateWithLifecycle(null)

    content(product)
}
