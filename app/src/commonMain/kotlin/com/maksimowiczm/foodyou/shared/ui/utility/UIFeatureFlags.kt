package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*

@Immutable
data class UIFeatureFlags(
    /** Whether to use a gradient painter for the app logo. */
    val gradientPainterLogo: Boolean = false,

    /**
     * Whether to download images from Open Food Facts in the search screen.
     *
     * When false, the app will not download any new images from Open Food Facts while searching and
     * displaying many products at once, but it will still display images that are already cached.
     *
     * This is intended to prevent DDOSing Open Food Facts servers during search usage. Note that
     * images will still be downloaded on other screens.
     */
    val downloadOpenFoodFactsSearchImages: Boolean = false,

    /** Whether to hide all multiple profile features */
    val singleProfileMode: Boolean = false,

    /** Whether to display exact timestamps for food diary entries. */
    val foodDiaryEntryTimestamps: Boolean = true,
)

val LocalUIFeatureFlags = compositionLocalOf { UIFeatureFlags() }
