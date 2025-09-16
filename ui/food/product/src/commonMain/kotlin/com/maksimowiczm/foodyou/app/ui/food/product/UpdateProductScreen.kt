package com.maksimowiczm.foodyou.app.ui.food.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.food.product.update.UpdateProductScreen
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateProductScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    productId: FoodId.Product,
    modifier: Modifier = Modifier,
) {
    UpdateProductScreen(
        onBack = onBack,
        onUpdate = onUpdate,
        viewModel = koinViewModel(parameters = { parametersOf(productId) }),
        modifier = modifier,
    )
}
