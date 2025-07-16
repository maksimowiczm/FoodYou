package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.about.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.about.PreviewReleaseDialog
import com.maksimowiczm.foodyou.feature.language.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun FoodYouApp() {
    FoodYouTheme {
        Surface {
            FoodYouNavHost()
        }

        AppUpdateChangelogModalBottomSheet()
        TranslationWarningStartupDialog()
        PreviewReleaseDialog()
    }
}
