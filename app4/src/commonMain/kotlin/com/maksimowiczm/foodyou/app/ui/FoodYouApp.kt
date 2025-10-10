package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.app.ui.common.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouApp() {
    val appViewModel: AppViewModel = koinViewModel()

    val onboardingFinished = appViewModel.onboardingFinished.collectAsStateWithLifecycle().value

    FoodYouTheme {
        Surface {
            if (onboardingFinished == false) {
                Onboarding(onFinish = appViewModel::onFinishOnboarding)
            } else {
                FoodYouNavHost()
            }
        }
    }
}
