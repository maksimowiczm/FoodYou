package com.maksimowiczm.foodyou.app.ui.common.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouTheme(content: @Composable () -> Unit) {
    val viewModel: ThemeViewModel = koinViewModel()
    val themeSettings = viewModel.themeSettings.collectAsStateWithLifecycle()
    FoodYouTheme(themeSettings.value, content)
}

@Composable
fun PreviewFoodYouTheme(content: @Composable () -> Unit) {
    FoodYouTheme(themeSettings = null, content = content)
}

@Composable
internal expect fun FoodYouTheme(themeSettings: ThemeSettings?, content: @Composable () -> Unit)

val MaterialDeepPurple = Color(0xFF6200EE)
