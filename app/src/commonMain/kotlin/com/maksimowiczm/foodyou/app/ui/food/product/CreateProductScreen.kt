package com.maksimowiczm.foodyou.app.ui.food.product

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.food.product.create.CreateProductApp
import com.maksimowiczm.foodyou.app.ui.food.product.create.CreateProductEvent
import com.maksimowiczm.foodyou.app.ui.food.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Product) -> Unit,
    modifier: Modifier = Modifier,
    url: String? = null,
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
        url = url,
    )
}
