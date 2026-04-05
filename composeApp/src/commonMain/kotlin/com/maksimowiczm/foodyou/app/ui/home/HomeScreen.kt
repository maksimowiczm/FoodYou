package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneState
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.navigation.Crossfade
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfade
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeIn
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeOut
import com.maksimowiczm.foodyou.app.ui.common.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtectionDefaults.rememberScrollConnection
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchViewModel
import com.maksimowiczm.foodyou.app.ui.home.calendar.CalendarCard
import com.maksimowiczm.foodyou.app.ui.home.common.rememberHomeState
import com.maksimowiczm.foodyou.app.ui.home.search.CollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.FavoriteCollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.FoodDataCentralCollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.Home
import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchScreen
import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchView
import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchViewModel
import com.maksimowiczm.foodyou.app.ui.home.search.OpenFoodFactsCollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.Search
import com.maksimowiczm.foodyou.app.ui.home.search.SearchFilterChips
import com.maksimowiczm.foodyou.app.ui.home.search.SearchNavigationBackHandler
import com.maksimowiczm.foodyou.app.ui.home.search.SearchView
import com.maksimowiczm.foodyou.app.ui.home.search.YourFoodCollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.rememberSearchState
import com.maksimowiczm.foodyou.common.extension.safeRemoveLast
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onAvatar: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserFood: (UserProductIdentity) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchState = rememberSearchState()
    val collections = rememberCollectionFilters()

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val scrollConnection = rememberScrollConnection { offset.floatValue -= it.y }
    val topBarHeight = density.run { 64.dp.toPx() }

    val homeSearchViewModel: HomeSearchViewModel = koinViewModel()
    val userFoodSearchViewModel: UserFoodSearchViewModel = koinViewModel()
    val openFoodFactsSearchViewModel: OpenFoodFactsSearchViewModel = koinViewModel()
    val foodDataCentralSearchViewModel: FoodDataCentralSearchViewModel = koinViewModel()
    val favoriteFoodSearchViewModel: FavoriteFoodSearchViewModel = koinViewModel()
    LaunchedCollectWithLifecycle(homeSearchViewModel.searchQuery) {
        userFoodSearchViewModel.search(it)
        openFoodFactsSearchViewModel.search(it)
        foodDataCentralSearchViewModel.search(it)
        favoriteFoodSearchViewModel.search(it)
    }

    LaunchedEffect(searchState.isHome) {
        if (searchState.isHome) {
            searchState.popToHome()
            homeSearchViewModel.search(null)
        }
    }

    if (searchState.showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            visible = searchState.showBarcodeScanner,
            onClose = { searchState.showBarcodeScanner = false },
            onBarcodeScan = {
                scope.launch { searchState.goToSearch(query = it) }
                homeSearchViewModel.search(it)
            },
        )
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollConnection),
        topBar = {
            val profileViewModel: ProfileViewModel = koinViewModel()
            val profiles by profileViewModel.profiles.collectAsStateWithLifecycle()
            val selectedProfileId by profileViewModel.selectedProfile.collectAsStateWithLifecycle()
            val profile =
                remember(profiles, selectedProfileId) {
                    profiles?.find { it.id == selectedProfileId }
                }

            HomeScreenTopBar(
                profile = profile,
                profiles = profiles ?: emptyList(),
                homeSearchState = searchState,
                onAvatar = onAvatar,
                onSearch = {
                    scope.launch { searchState.goToSearch() }
                    homeSearchViewModel.search(it)
                },
                onSelectProfile = profileViewModel::selectProfile,
            )
        },
    ) { contentPadding ->
        val filterChipsHeight = remember { mutableFloatStateOf(0f) }

        val entries =
            rememberDecoratedNavEntries(
                backStack = searchState.backStack,
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                entryProvider =
                    entryProvider {
                        entry<Home>(metadata = crossfadeTransitionMetadata) {
                            val homeState = rememberHomeState(LocalDate.now())

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = contentPadding.add(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                item {
                                    CalendarCard(
                                        homeState = homeState,
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                    )
                                }
                            }
                        }
                        entry<SearchView>(metadata = transitionSearchMetadata) {
                            HomeSearchView(
                                contentPadding =
                                    contentPadding.add(
                                        top = density.run { filterChipsHeight.floatValue.toDp() }
                                    ),
                                homeSearchState = searchState,
                                onSearch = {
                                    scope.launch { searchState.goToSearch(it) }
                                    homeSearchViewModel.search(it)
                                },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        entry<Search>(metadata = transitionSearchMetadata) {
                            HomeSearchScreen(
                                contentPadding =
                                    contentPadding.add(
                                        top = density.run { filterChipsHeight.floatValue.toDp() }
                                    ),
                                homeSearchState = searchState,
                                onFoodDataCentralProduct = onFoodDataCentralProduct,
                                onOpenFoodFactsProduct = onOpenFoodFactsProduct,
                                onUserFood = onUserFood,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    },
            )

        val sceneState =
            rememberSceneState(
                entries = entries,
                sceneStrategy = SinglePaneSceneStrategy(),
                onBack = { searchState.backStack.safeRemoveLast() },
            )

        val navigationEventState =
            rememberNavigationEventState(
                currentInfo = SceneInfo(sceneState.currentScene),
                backInfo = sceneState.previousScenes.map(::SceneInfo),
            )

        SearchNavigationBackHandler(
            homeSearchState = searchState,
            sceneState = sceneState,
            navigationEventState = navigationEventState,
        )

        NavDisplay(sceneState = sceneState, navigationEventState = navigationEventState)

        val isSearch = animateFloatAsState(if (!searchState.isHome) 1f else 0f)
        Box(
            Modifier.fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding())
                .graphicsLayer {
                    alpha =
                        when {
                            searchState.isHome -> 0f
                            searchState.backProgressAnimatable.value != 0f ->
                                (1 -
                                    searchState.backProgressAnimatable.value /
                                        Crossfade.PROGRESS_THRESHOLD)

                            else -> isSearch.value
                        }
                    translationY = (1 - isSearch.value) * size.height
                    filterChipsHeight.floatValue = size.height
                }
        ) {
            SearchFilterChips(
                selectedCollection = searchState.collection,
                onSelectCollection = { searchState.collection = it },
                collections = collections,
                contentPadding = PaddingValues(horizontal = 16.dp),
            )
        }
    }

    HomeModalWideNavigationRail(
        homeSearchState = searchState,
        collections = collections,
        onCreate = {
            scope.launch { searchState.railState.collapse() }
            onCreate()
        },
    )

    StatusBarProtection { (offset.value / topBarHeight).coerceIn(0f, 1f) }
}

@Composable
private fun rememberCollectionFilters(): List<CollectionFilter> {
    val showOpenFoodFacts =
        koinViewModel<OpenFoodFactsSearchViewModel>()
            .shouldShowFilter
            .collectAsStateWithLifecycle(false)
            .value
    val showFoodDataCentral =
        koinViewModel<FoodDataCentralSearchViewModel>()
            .shouldShowFilter
            .collectAsStateWithLifecycle(false)
            .value

    return remember(showOpenFoodFacts, showFoodDataCentral) {
        buildList {
            add(FavoriteCollectionFilter)
            add(YourFoodCollectionFilter())
            if (showOpenFoodFacts) add(OpenFoodFactsCollectionFilter())
            if (showFoodDataCentral) add(FoodDataCentralCollectionFilter())
        }
    }
}

private val crossfadeTransitionMetadata =
    NavDisplay.transitionSpec { crossfade() } +
        NavDisplay.popTransitionSpec { crossfade() } +
        NavDisplay.predictivePopTransitionSpec { crossfade() }

private val transitionSearchMetadata =
    NavDisplay.transitionSpec {
        slideInVertically(initialOffsetY = { (it * 0.05f).toInt() }) + crossfadeIn() togetherWith
            crossfadeOut()
    } +
        NavDisplay.popTransitionSpec { crossfade() } +
        NavDisplay.predictivePopTransitionSpec { crossfade() }
