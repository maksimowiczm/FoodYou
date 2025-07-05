package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.product.update.UpdateProductScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateProductScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    productId: FoodId.Product,
    modifier: Modifier = Modifier
) {
    UpdateProductScreen(
        onBack = onBack,
        onUpdate = onUpdate,
        viewModel = koinViewModel(
            parameters = { parametersOf(productId) }
        ),
        modifier = modifier
    )
}
