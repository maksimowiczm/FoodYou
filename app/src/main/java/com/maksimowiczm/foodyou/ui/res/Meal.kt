package com.maksimowiczm.foodyou.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal

@Composable
fun Meal.stringResource(): String {
    return when (this) {
        Meal.Breakfast -> androidx.compose.ui.res.stringResource(R.string.meal_name_breakfast)
        Meal.Lunch -> androidx.compose.ui.res.stringResource(R.string.meal_name_lunch)
        Meal.Dinner -> androidx.compose.ui.res.stringResource(R.string.meal_name_dinner)
        Meal.Snacks -> androidx.compose.ui.res.stringResource(R.string.meal_name_snacks)
    }
}
