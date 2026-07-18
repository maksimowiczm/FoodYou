package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*

interface UIFeatureFlags {
    /** Whether to use a gradient painter for the app logo. */
    val gradientPainterLogo: Boolean

    // Hardcode for now
    companion object : UIFeatureFlags {
        override val gradientPainterLogo: Boolean = false
    }
}

val LocalUIFeatureFlags = compositionLocalOf<UIFeatureFlags> { UIFeatureFlags }
