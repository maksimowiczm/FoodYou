package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.navigation.FoodYouAppNavHost
import com.maksimowiczm.foodyou.app.ui.changelog.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.app.ui.changelog.PreviewReleaseDialog
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyFormatterProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.NutrientsOrderProvider
import com.maksimowiczm.foodyou.app.ui.language.TranslationWarningStartupDialog
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouApp(onDeveloperOptions: () -> Unit) {
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
                        FoodYouAppNavHost(onDeveloperOptions)
                        AppUpdateChangelogModalBottomSheet()
                    }
                } else {
                    Onboarding(onFinish = viewModel::finishOnboarding)
                }
            }
        }
    }
}
