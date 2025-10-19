package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHost
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute.FoodDatabase
import com.maksimowiczm.foodyou.app.navigation.navigateSingleTop
import com.maksimowiczm.foodyou.app.ui.common.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyFormatterProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.NutrientsOrderProvider
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouApp(userQuery: String?) {
    val appViewModel: AppViewModel = koinViewModel()

    val onboardingFinished = appViewModel.onboardingFinished.collectAsStateWithLifecycle().value
    val nutrientsOrder = appViewModel.nutrientsOrder.collectAsStateWithLifecycle().value
    val energyFormatter = appViewModel.energyFormatter.collectAsStateWithLifecycle().value

    NutrientsOrderProvider(nutrientsOrder) {
        EnergyFormatterProvider(energyFormatter) {
            FoodYouTheme {
                Surface {
                    if (onboardingFinished == false) {
                        Onboarding(onFinish = appViewModel::onFinishOnboarding)
                    } else {
                        val navController = rememberNavController()

                        LaunchedEffect(navController) {
                            if (userQuery != null) {
                                navController.navigateSingleTop(FoodDatabase(userQuery))
                            }
                        }

                        FoodYouNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
