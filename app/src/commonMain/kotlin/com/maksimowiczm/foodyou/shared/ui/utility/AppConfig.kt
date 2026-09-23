package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig

val LocalAppConfig = staticCompositionLocalOf { FoodYouConfig("4.x.x-preview") }
