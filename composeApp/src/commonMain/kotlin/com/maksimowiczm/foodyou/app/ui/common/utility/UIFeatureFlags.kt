package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*

interface UIFeatureFlags {
    /** Whether to use a gradient painter for the app logo. */
    val gradientPainterLogo: Boolean

    /**
     * Whether to download images from Open Food Facts in the search screen.
     *
     * When false, the app will not download any new images from Open Food Facts while searching and
     * displaying many products at once, but it will still display images that are already cached.
     *
     * This is intended to prevent DDOSing Open Food Facts servers during search usage. Note that
     * images will still be downloaded on other screens.
     */
    val downloadOpenFoodFactsSearchImages: Boolean

    // Hardcode for now
    companion object : UIFeatureFlags {
        override val gradientPainterLogo: Boolean = false
        override val downloadOpenFoodFactsSearchImages: Boolean = true
    }
}

val LocalUIFeatureFlags = compositionLocalOf<UIFeatureFlags> { UIFeatureFlags }
