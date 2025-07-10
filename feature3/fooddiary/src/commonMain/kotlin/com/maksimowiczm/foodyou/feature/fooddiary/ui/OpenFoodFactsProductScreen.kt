package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsProductScreen

@Composable
fun OpenFoodFactsProductScreen(
    onBack: () -> Unit,
    onImport: (localProductId: FoodId.Product) -> Unit,
    productId: Long,
    modifier: Modifier = Modifier
) {
    OpenFoodFactsProductScreen(
        onBack = onBack,
        onImport = onImport,
        productId = productId,
        modifier = modifier
    )
}
