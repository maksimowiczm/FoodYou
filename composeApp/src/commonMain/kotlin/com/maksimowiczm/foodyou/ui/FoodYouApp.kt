package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.feature.about.master.ui.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.about.master.ui.PreviewReleaseDialog
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.shared.ui.theme.FoodYouTheme

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun FoodYouApp() {
    FoodYouTheme {
        Surface {
            FoodYouNavHost()
            AppUpdateChangelogModalBottomSheet()
        }

        // TranslationWarningStartupDialog()
        PreviewReleaseDialog()
    }
}
