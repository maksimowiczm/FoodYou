package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.app.navigation.ForwardBackwardTransition
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Onboarding(onFinish: (LocalAccountId) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: OnboardingViewModel = koinViewModel()

    @Suppress("UNCHECKED_CAST")
    val backstack = rememberNavBackStack(config, BeforeYouStart) as NavBackStack<OnboardingNavKey>

    LaunchedCollectWithLifecycle(viewModel.events) { event ->
        when (event) {
            is OnboardingEvent.Finished -> onFinish(event.localAccountId)
        }
    }

    val finishingOnboarding = viewModel.finishingOnboarding.collectAsStateWithLifecycle().value
    LaunchedEffect(finishingOnboarding, backstack) {
        if (finishingOnboarding) {
            if (backstack.lastOrNull() !is AlmostDone) {
                backstack.add(AlmostDone)
            }
        }
    }

    NavDisplay(
        backStack = backstack,
        modifier = modifier,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        transitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.enterTransition(),
                ForwardBackwardTransition.exitTransition(),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
        predictivePopTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
    ) { key ->
        when (key) {
            AddProfile ->
                NavEntry(key) {
                    AddProfileScreen(
                        viewModel = viewModel,
                        onBack = { backstack.removeLastIf<AddProfile>() },
                        onContinue = viewModel::finishOnboarding,
                    )
                }

            AlmostDone ->
                NavEntry(key) {
                    NavigationBackHandler(
                        state = rememberNavigationEventState(NavigationEventInfo.None),
                        onBackCompleted = {},
                    )
                    AlmostDoneScreen()
                }

            BeforeYouStart ->
                NavEntry(key) { BeforeYouStartScreen(onContinue = { backstack.add(FoodDatabase) }) }

            FoodDatabase ->
                NavEntry(key) {
                    FoodDatabaseScreen(
                        viewModel = viewModel,
                        onBack = { backstack.removeLastIf<FoodDatabase>() },
                        onContinue = { backstack.add(AddProfile) },
                    )
                }
        }
    }
}

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AddProfile.serializer())
            subclass(AlmostDone.serializer())
            subclass(BeforeYouStart.serializer())
            subclass(FoodDatabase.serializer())
        }
    }
}

@Serializable private sealed interface OnboardingNavKey : NavKey

@Serializable private object AddProfile : OnboardingNavKey

@Serializable private object AlmostDone : OnboardingNavKey

@Serializable private object BeforeYouStart : OnboardingNavKey

@Serializable private object FoodDatabase : OnboardingNavKey
