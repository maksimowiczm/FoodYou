package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.app.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.app.navigation.navigateSingleTop
import com.maksimowiczm.foodyou.app.navigation.popBackStackInclusive
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Onboarding(onFinish: (LocalAccountId) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val navController = rememberNavController()

    LaunchedCollectWithLifecycle(viewModel.events) { event ->
        when (event) {
            is OnboardingEvent.Finished -> onFinish(event.localAccountId)
        }
    }

    val finishingOnboarding = viewModel.finishingOnboarding.collectAsStateWithLifecycle().value
    LaunchedEffect(finishingOnboarding) {
        if (finishingOnboarding) {
            if (navController.currentDestination?.hasRoute<AlmostDone>() == false) {
                navController.navigateSingleTop(AlmostDone)
            }
        }
    }

    NavHost(navController = navController, startDestination = BeforeYouStart, modifier = modifier) {
        forwardBackwardComposable<BeforeYouStart> {
            BeforeYouStartScreen(
                viewModel = viewModel,
                onContinue = { navController.navigateSingleTop(FoodDatabase) },
            )
        }
        forwardBackwardComposable<FoodDatabase> {
            FoodDatabaseScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStackInclusive<FoodDatabase>() },
                onContinue = { navController.navigateSingleTop(AddProfile) },
            )
        }
        forwardBackwardComposable<AddProfile> {
            AddProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStackInclusive<AddProfile>() },
                onContinue = viewModel::finishOnboarding,
            )
        }
        forwardBackwardComposable<AlmostDone> {
            BackHandler {}
            AlmostDoneScreen()
        }
    }
}

@Serializable private object BeforeYouStart

@Serializable private object FoodDatabase

@Serializable private object AddProfile

@Serializable private object AlmostDone
