package com.maksimowiczm.foodyou.ui

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryScreen
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun FoodYouApp() {
    FoodYouTheme {
        DiaryScreen()
    }
}
