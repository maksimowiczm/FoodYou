package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavDisplay
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoute
import com.maksimowiczm.foodyou.app.navigation.rememberFoodYouNavBackStack
import com.maksimowiczm.foodyou.app.ui.common.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyUnitProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.NutrientsOrderProvider
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding
import com.maksimowiczm.foodyou.common.extension.safeRemoveLast
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoodYouApp(userQuery: String?) {
    val appViewModel: AppViewModel = koinViewModel()
    val networkConfig: NetworkConfig = koinInject()

    val nutrientsOrder = appViewModel.nutrientsOrder.collectAsStateWithLifecycle().value
    val energyUnit = appViewModel.energyUnit.collectAsStateWithLifecycle().value
    val appPage by appViewModel.appPage.collectAsStateWithLifecycle()

    setSingletonImageLoaderFactory { context ->
        val httpClient = HttpClient { install(UserAgent) { agent = networkConfig.userAgent } }

        @OptIn(ExperimentalCoilApi::class)
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
                add(KtorNetworkFetcherFactory(httpClient))
            }
            .build()
    }

    NutrientsOrderProvider(nutrientsOrder) {
        EnergyUnitProvider(energyUnit) {
            FoodYouTheme {
                Surface {
                    when (appPage) {
                        AppPage.Splash -> SplashScreen()

                        AppPage.Onboarding ->
                            Onboarding(onFinish = appViewModel::onFinishOnboarding)

                        AppPage.Main -> {
                            val backStack =
                                rememberFoodYouNavBackStack(FoodYouNavHostRoute.Home(null))

                            LaunchedEffect(backStack, userQuery) {
                                if (userQuery == null) return@LaunchedEffect

                                val home =
                                    backStack.first() as? FoodYouNavHostRoute.Home
                                        ?: return@LaunchedEffect

                                if (home.initialQuery != userQuery) {
                                    while (backStack.last() !is FoodYouNavHostRoute.Home) {
                                        backStack.safeRemoveLast()
                                    }

                                    backStack.safeRemoveLast()
                                    backStack.add(FoodYouNavHostRoute.Home(userQuery))
                                }
                            }

                            FoodYouNavDisplay(backStack)
                        }
                    }
                }
            }
        }
    }
}
