package com.maksimowiczm.foodyou.feature.food.product.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.product.presentation.create.CreateProductEvent
import com.maksimowiczm.foodyou.feature.food.product.presentation.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.food.product.ui.create.CreateProductApp
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Product) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
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
        onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        modifier = modifier,
        url = url,
    )
}
