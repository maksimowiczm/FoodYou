package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.about.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.language.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.feature.onboarding.preferences.ShowOnboardingPreference
import com.maksimowiczm.foodyou.feature.onboarding.ui.Onboarding
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun FoodYouApp(showOnBoardingPreference: ShowOnboardingPreference = userPreference()) {
    val showOnBoarding =
        showOnBoardingPreference.collectAsStateWithLifecycle(
            showOnBoardingPreference.getBlocking()
        ).value

    FoodYouTheme {
        Surface {
            if (showOnBoarding) {
                Onboarding(
                    onFinish = {
                        showOnBoardingPreference.setBlocking(false)
                    }
                )
            } else {
                FoodYouNavHost()
                AppUpdateChangelogModalBottomSheet()
            }
        }

        TranslationWarningStartupDialog()
//        PreviewReleaseDialog()
    }
}
