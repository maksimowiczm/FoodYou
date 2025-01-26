package com.maksimowiczm.foodyou.feature.addfood.ui

import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal

object AddFoodSharedTransitionKeys {
    data class SearchScreen(
        val meal: Meal
    )

    // Internal, should not be used outside of this feature
    internal const val BARCODE_SCANNER = "BARCODE_SCANNER"
}
