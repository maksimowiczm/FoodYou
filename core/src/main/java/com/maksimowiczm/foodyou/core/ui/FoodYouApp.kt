package com.maksimowiczm.foodyou.core.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

@Suppress("ktlint:compose:modifier-missing-check")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodYouApp(homeFeatures: List<Feature.Home>, settingsFeatures: List<Feature.Settings>) {
    FoodYouTheme {
        Surface {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this
                ) {
                    FoodYouNavHost(
                        homeFeatures = homeFeatures,
                        settingsFeatures = settingsFeatures
                    )
                }
            }
        }
    }
}
