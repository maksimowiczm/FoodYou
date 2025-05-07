package com.maksimowiczm.foodyou.feature.meal.ui.screen

import com.maksimowiczm.foodyou.core.domain.model.FoodId

object MealScreenTestTags {

    data class FoodItem(val foodId: FoodId)

    const val FOOD_ITEMS = "food_items"
    const val ADD_FOOD_FAB = "add_food_fab"
    const val BARCODE_SCANNER_FAB = "barcode_scanner_fab"
    const val BOTTOM_SHEET = "bottom_sheet"
    const val SNACKBAR = "snackbar"
}
