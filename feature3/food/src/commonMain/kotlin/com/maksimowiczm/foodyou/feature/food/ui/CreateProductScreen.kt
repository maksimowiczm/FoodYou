package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductApp
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductEvent
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Product) -> Unit,
    modifier: Modifier = Modifier,
    url: String? = null
) {
    val viewModel: CreateProductViewModel = koinViewModel()

    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.events) { event ->
        when (event) {
            is CreateProductEvent.Created -> latestOnCreate(event.productId)
        }
    }

    CreateProductApp(
        onBack = onBack,
        onCreate = viewModel::createProduct,
        modifier = modifier,
        url = url
    )
}
