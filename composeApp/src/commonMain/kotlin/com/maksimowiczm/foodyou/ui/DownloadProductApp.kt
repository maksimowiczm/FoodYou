package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.shared.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.onboarding.ui.Onboarding
import com.maksimowiczm.foodyou.navigation.DownloadProductNavHost
import com.maksimowiczm.foodyou.presentation.AppViewModel
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
                DownloadProductNavHost(onBack = onBack, onCreate = onCreate, url = url)
            }
        }
    }
}
