package com.maksimowiczm.foodyou.ui

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost

@Composable
fun FoodYouApp() {
    FoodYouTheme {
        FoodYouNavHost()
    }
}
