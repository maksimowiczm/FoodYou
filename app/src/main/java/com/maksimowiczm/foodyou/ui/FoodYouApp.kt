package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun FoodYouApp() {
    FoodYouTheme {
        Surface {
            FoodYouNavHost()
        }
    }
}
