package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductScreen

@Composable
fun CreateProductScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Product) -> Unit,
    modifier: Modifier = Modifier
) {
    CreateProductScreen(onBack, onCreate, modifier)
}
