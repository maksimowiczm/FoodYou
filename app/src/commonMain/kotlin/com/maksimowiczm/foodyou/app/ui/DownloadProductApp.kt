package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.navigation.DownloadProductAppNavHost
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadProductApp(onBack: () -> Unit, onCreate: () -> Unit, url: String) {
    val viewModel: AppViewModel = koinViewModel()
    val onboardingFinished by viewModel.onboardingFinished.collectAsStateWithLifecycle()

    FoodYouTheme {
        Surface {
            if (!onboardingFinished) {
                Onboarding(onFinish = viewModel::finishOnboarding)
            } else {
                DownloadProductAppNavHost(onBack = onBack, onCreate = onCreate, url = url)
            }
        }
    }
}
