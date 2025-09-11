package com.maksimowiczm.foodyou.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.shared.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.shared.utility.EnergyFormatterProvider
import com.maksimowiczm.foodyou.feature.about.master.ui.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.about.master.ui.PreviewReleaseDialog
import com.maksimowiczm.foodyou.feature.onboarding.ui.Onboarding
import com.maksimowiczm.foodyou.feature.settings.language.ui.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.feature.shared.ui.NutrientsOrderProvider
import com.maksimowiczm.foodyou.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.presentation.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouApp() {
    val viewModel: AppViewModel = koinViewModel()
    val nutrientsOrder by viewModel.nutrientsOrder.collectAsStateWithLifecycle()
    val onboardingFinished by viewModel.onboardingFinished.collectAsStateWithLifecycle()
    val energyFormatter by viewModel.energyFormatter.collectAsStateWithLifecycle()

    NutrientsOrderProvider(nutrientsOrder) {
        EnergyFormatterProvider(energyFormatter) {
            FoodYouTheme {
                PreviewReleaseDialog()
                TranslationWarningStartupDialog()

                if (onboardingFinished) {
                    Surface {
                        FoodYouNavHost()
                        AppUpdateChangelogModalBottomSheet()
                    }
                } else {
                    Onboarding(onFinish = viewModel::finishOnboarding)
                }
            }
        }
    }
}
