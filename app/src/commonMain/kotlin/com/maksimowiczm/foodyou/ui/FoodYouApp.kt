package com.maksimowiczm.foodyou.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.changelog.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.language.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost

@Suppress("ktlint:compose:modifier-missing-check")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodYouApp() {
    FoodYouTheme {
        Surface {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalNavigationSharedTransitionScope provides this
                ) {
                    FoodYouNavHost()
                }
            }

            TranslationWarningStartupDialog()
            AppUpdateChangelogModalBottomSheet()
        }
    }
}
