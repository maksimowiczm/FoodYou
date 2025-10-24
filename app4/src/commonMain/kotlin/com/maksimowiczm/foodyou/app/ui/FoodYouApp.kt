package com.maksimowiczm.foodyou.app.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    val nutrientsOrder = appViewModel.nutrientsOrder.collectAsStateWithLifecycle().value
    val energyFormatter = appViewModel.energyFormatter.collectAsStateWithLifecycle().value
    val appPage by appViewModel.appPage.collectAsStateWithLifecycle()

    NutrientsOrderProvider(nutrientsOrder) {
        EnergyFormatterProvider(energyFormatter) {
            FoodYouTheme {
                Surface {
                    when (appPage) {
                        AppPage.Splash -> {
                            // TODO
                            Spacer(Modifier.fillMaxSize())
                        }

                        AppPage.Onboarding ->
                            Onboarding(onFinish = appViewModel::onFinishOnboarding)

                        AppPage.Main -> {
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
}
