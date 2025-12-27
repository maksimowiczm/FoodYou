package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.app.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Onboarding(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val viewModel: OnboardingViewModel = koinViewModel()

    val latestOnFinish by rememberUpdatedState(onFinish)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            is OnboardingEvent.Finished -> latestOnFinish()
        }
    }

    NavHost(navController = navController, startDestination = BeforeYouStart, modifier = modifier) {
        forwardBackwardComposable<BeforeYouStart> {
            BeforeYouStartScreen(
                onAgree = {
                    viewModel.finish() // Start TBCA import and finish onboarding
                    navController.navigate(AlmostDone) {
                        launchSingleTop = true
                        popUpTo(BeforeYouStart) { inclusive = true }
                    }
                }
            )
        }
        forwardBackwardComposable<AlmostDone> { AlmostDoneScreen() }
    }
}

@Serializable private data object BeforeYouStart

@Serializable private data object AlmostDone
