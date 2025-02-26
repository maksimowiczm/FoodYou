package com.maksimowiczm.foodyou.feature.home.caloriescard

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.home.caloriescard.ui.CaloriesCard

object CaloriesCard : Feature.Home {
    override fun build(navController: NavController) = HomeFeature { _, modifier, homeState ->
        CaloriesCard(
            homeState = homeState,
            modifier = modifier
        )
    }
}
