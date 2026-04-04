package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.SceneState
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.NavigationEventState
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.common.extension.safeRemoveLast
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Stable
internal class HomeSearchState(
    collectionState: MutableState<CollectionFilter>,
    val backProgressAnimatable: Animatable<Float, AnimationVector1D>,
    showBarcodeScannerState: MutableState<Boolean>,
    val backStack: NavBackStack<NavKey>,
    val textFieldState: TextFieldState,
    val railState: WideNavigationRailState,
    val listStates: ListStates,
) {
    var collection by collectionState
    var showBarcodeScanner by showBarcodeScannerState

    val isHome by derivedStateOf { backStack.last() is Home }

    val showSearchField by derivedStateOf { backStack.last() !is Home }

    suspend fun popToHome() {
        while (backStack.last() !is Home) {
            backStack.safeRemoveLast()
        }

        textFieldState.clearText()
        railState.collapse()
    }

    suspend fun goToSearchView() {
        if (backStack.last() !is SearchView) {
            backStack.add(SearchView)
        }

        railState.collapse()
    }

    suspend fun goToSearch(
        query: String = textFieldState.text.toString(),
        collectionFilter: CollectionFilter = collection,
    ) {
        collection = collectionFilter
        textFieldState.setTextAndPlaceCursorAtEnd(query)
        showBarcodeScanner = false

        if (backStack.last() !is Search) {
            backStack.removeLastIf<SearchView>()
            backStack.add(Search)
        }

        railState.collapse()
    }
}

@Composable
internal fun rememberSearchState(): HomeSearchState {
    val backProgressAnimatable = remember { Animatable(0f) }

    val backStack = rememberNavBackStack(config, Home)

    val collectionState = rememberSerializable {
        mutableStateOf<CollectionFilter>(YourFoodCollectionFilter())
    }

    val textFieldState = rememberTextFieldState()

    val railState = rememberWideNavigationRailState()

    val showBarcodeScannerState = rememberSaveable { mutableStateOf(false) }

    val listStates = rememberListStates()

    return remember(
        collectionState,
        backProgressAnimatable,
        showBarcodeScannerState,
        backStack,
        textFieldState,
        railState,
        listStates,
    ) {
        HomeSearchState(
            collectionState,
            backProgressAnimatable,
            showBarcodeScannerState,
            backStack,
            textFieldState,
            railState,
            listStates,
        )
    }
}

@Immutable
internal class ListStates(
    val favorite: LazyListState,
    val userFood: LazyListState,
    val openFoodFacts: LazyListState,
    val foodDataCentral: LazyListState,
)

@Composable
private fun rememberListStates(): ListStates {
    val favorite = rememberLazyListState()
    val yourFood = rememberLazyListState()
    val openFoodFacts = rememberLazyListState()
    val usda = rememberLazyListState()

    return remember(favorite, yourFood, openFoodFacts, usda) {
        ListStates(
            favorite = favorite,
            userFood = yourFood,
            openFoodFacts = openFoodFacts,
            foodDataCentral = usda,
        )
    }
}

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class)
            subclass(SearchView::class)
            subclass(Search::class)
        }
    }
}

@Serializable internal object Home : NavKey

@Serializable internal object SearchView : NavKey

@Serializable internal object Search : NavKey

@Composable
internal fun SearchNavigationBackHandler(
    homeSearchState: HomeSearchState,
    sceneState: SceneState<*>,
    navigationEventState: NavigationEventState<SceneInfo<NavKey>>,
    animationSpec: AnimationSpec<Float> = MaterialTheme.motionScheme.fastSpatialSpec(),
) {
    val scope = rememberCoroutineScope()

    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = sceneState.currentScene.previousEntries.isNotEmpty(),
        onBackCancelled = {
            scope.launch { homeSearchState.backProgressAnimatable.animateTo(0f, animationSpec) }
        },
        onBackCompleted = {
            scope.launch {
                homeSearchState.backProgressAnimatable.animateTo(1f, animationSpec) {
                    scope.launch { snapTo(0f) }
                }
            }
            homeSearchState.backStack.removeLastIf { true }
        },
    )

    LaunchedEffect(navigationEventState.transitionState) {
        val transitionState = navigationEventState.transitionState
        if (transitionState is NavigationEventTransitionState.InProgress) {
            homeSearchState.backProgressAnimatable.snapTo(transitionState.latestEvent.progress)
        }
    }
}
