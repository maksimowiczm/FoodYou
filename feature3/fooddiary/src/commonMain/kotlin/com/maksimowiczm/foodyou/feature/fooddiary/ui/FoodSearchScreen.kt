package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.FoodSearchScreen
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodSearchScreen(
    mealId: Long,
    date: LocalDate,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onOpenFoodFactsProduct: (id: Long) -> Unit,
    onFood: (id: FoodId) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    FoodSearchScreen(
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onOpenFoodFactsProduct = { onOpenFoodFactsProduct(it.id) },
        onFood = { onFood(it.id) },
        viewModel = koinViewModel(
            parameters = { parametersOf(mealId, date) }
        ),
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
