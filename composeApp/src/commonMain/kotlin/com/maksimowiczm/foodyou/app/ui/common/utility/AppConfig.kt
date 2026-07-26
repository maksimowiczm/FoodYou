package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig

val LocalAppConfig = staticCompositionLocalOf { FoodYouConfig("4.x.x-preview") }
