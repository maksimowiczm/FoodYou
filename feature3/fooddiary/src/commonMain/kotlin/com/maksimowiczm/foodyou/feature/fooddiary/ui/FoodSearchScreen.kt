package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    FoodSearchScreen(
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onOpenFoodFactsProduct = {
            // TODO
        },
        onFood = {
            // TODO
        },
        viewModel = koinViewModel(
            parameters = { parametersOf(mealId, date) }
        ),
        modifier = modifier
    )
}
