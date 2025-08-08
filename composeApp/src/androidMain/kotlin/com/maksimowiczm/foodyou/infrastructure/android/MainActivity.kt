package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.compose.material3.Surface
import com.maksimowiczm.foodyou.feature.about.master.ui.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.about.master.ui.PreviewReleaseDialog
import com.maksimowiczm.foodyou.feature.settings.language.ui.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.shared.ui.theme.FoodYouTheme

class MainActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodYouTheme {
                PreviewReleaseDialog()
                TranslationWarningStartupDialog()

                Surface {
                    FoodYouNavHost()
                    AppUpdateChangelogModalBottomSheet()
                }
            }
        }
    }
}
