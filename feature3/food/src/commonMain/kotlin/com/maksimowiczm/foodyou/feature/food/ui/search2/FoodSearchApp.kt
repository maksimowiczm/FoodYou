package com.maksimowiczm.foodyou.feature.food.ui.search2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    excludedFood: FoodId.Recipe?,
    modifier: Modifier = Modifier
) {
    val viewModel: FoodSearchViewModel = koinViewModel {
        parametersOf(excludedFood)
    }
}
