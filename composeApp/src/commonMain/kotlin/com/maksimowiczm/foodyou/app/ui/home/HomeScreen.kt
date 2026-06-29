package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SceneInfo
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneState
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.app.navigation.Crossfade
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfade
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeIn
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfadeOut
import com.maksimowiczm.foodyou.app.ui.common.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.component.Scrim
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtectionDefaults.rememberScrollConnection
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.horizontal
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.maksimowiczm.foodyou.app.ui.common.extension.plus
import com.maksimowiczm.foodyou.app.ui.common.saveable.jsonSaver
import com.maksimowiczm.foodyou.app.ui.food.search.CollectionFilter
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchFloatingActionButton
import com.maksimowiczm.foodyou.app.ui.food.search.SearchCollection
import com.maksimowiczm.foodyou.app.ui.food.search.SearchFilters
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.rememberCollectionFilters
import com.maksimowiczm.foodyou.app.ui.home.calendar.CalendarCard
import com.maksimowiczm.foodyou.app.ui.home.common.rememberHomeState
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.common.extension.safeRemoveLast
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(
    onAvatar: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserProduct: (UserProductIdentity) -> Unit,
    onUserRecipe: (UserRecipeIdentity) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    initialQuery: String?,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel { parametersOf(initialQuery) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val motionScheme = MaterialTheme.motionScheme

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val topBarHeight = density.run { 64.dp.toPx() }
    val scrollConnection = rememberScrollConnection { offset.floatValue -= it.y }

    val textFieldState = rememberTextFieldState(initialQuery ?: "")

    val elements =
        remember(initialQuery) {
            listOfNotNull(HomeNavKey.Home, if (initialQuery != null) HomeNavKey.Search else null)
        }
    val backStack = rememberNavBackStack(config, *elements.toTypedArray<NavKey>())

    val isHome by remember { derivedStateOf { backStack.last() is HomeNavKey.Home } }
    val isSearch =
        animateFloatAsState(
            if (!isHome) 1f else 0f,
            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        )

    val backProgress = remember { Animatable(0f) }
    val homeProgress = remember { Animatable(if (isHome) 1f else 0f) }
    LaunchedEffect(isHome, backProgress.value) {
        val gesture = backProgress.value
        if (gesture > 0f) homeProgress.snapTo(gesture)
        else
            homeProgress.animateTo(
                targetValue = if (isHome) 1f else 0f,
                animationSpec = motionScheme.fastSpatialSpec(),
            )
    }

    val focusRequester = remember { FocusRequester() }

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val collections = rememberCollectionFilters()
    val selectedCollection =
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

    val railState = rememberWideNavigationRailState()

    val foodDataCentralExtension = searchViewModel.extension<FoodDataCentralSearchExtension>()
    val openFoodFactsExtension = searchViewModel.extension<OpenFoodFactsSearchExtension>()
    LaunchedEffect(selectedCollection.value) {
        when (val collection = selectedCollection.value) {
            is SearchCollection.FoodDataCentral ->
                foodDataCentralExtension.dataTypes(collection.dataTypes)

            is SearchCollection.OpenFoodFacts -> openFoodFactsExtension.version(collection.version)

            else -> Unit
        }
    }

    val showBarcodeScanner = rememberSaveable { mutableStateOf(false) }
    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner.value,
        onClose = { showBarcodeScanner.value = false },
        onBarcodeScan = {
            showBarcodeScanner.value = false
            searchViewModel.search(it)
            textFieldState.setTextAndPlaceCursorAtEnd(it)
            if (backStack.last() !is HomeNavKey.Search) backStack.add(HomeNavKey.Search)
        },
    )

    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    val fabInsets =
        WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    Box(modifier) {
        FoodSearchFloatingActionButton(
            fabExpanded = fabExpanded,
            onFabExpandedChange = { fabExpanded = it },
            onCreateRecipe = onCreateRecipe,
            onCreateProduct = onCreateProduct,
            modifier =
                Modifier.zIndex(100f)
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(fabInsets)
                    .consumeWindowInsets(fabInsets)
                    .graphicsLayer {
                        val progress = 1f - homeProgress.value
                        alpha =
                            when {
                                isHome -> 0f
                                backProgress.value != 0f ->
                                    (1 - backProgress.value / Crossfade.PROGRESS_THRESHOLD)
                                else -> isSearch.value
                            }
                        scaleX = 0.2f + 0.8f * progress
                        scaleY = 0.2f + 0.8f * progress
                        translationY = (1f - progress) * size.height / 4
                        translationX = (1f - progress) * size.width / 4
                    },
        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f),
        )
        Scaffold(
            modifier = Modifier.nestedScroll(scrollConnection),
            topBar = {
                val profileViewModel: ProfileViewModel = koinViewModel()
                val profiles by profileViewModel.profiles.collectAsStateWithLifecycle()
                val selectedProfileId by
                    profileViewModel.selectedProfile.collectAsStateWithLifecycle()
                val profile =
                    remember(profiles, selectedProfileId) {
                        profiles?.find { it.id == selectedProfileId }
                    }

                HomeScreenTopBar(
                    profile = profile,
                    profiles = profiles ?: emptyList(),
                    textFieldState = textFieldState,
                    homeProgress = { homeProgress.value },
                    onAvatar = onAvatar,
                    onSearch = {
                        if (backStack.last() !is HomeNavKey.Search) backStack.add(HomeNavKey.Search)
                        searchViewModel.search(it)
                    },
                    onSearchBar = {
                        if (backStack.last() !is HomeNavKey.Search) backStack.add(HomeNavKey.Search)
                        scope.launch {
                            delay(DefaultDurationMillis.milliseconds)
                            focusRequester.requestFocus()
                        }
                    },
                    onBack = { backStack.removeLastIf { it !is HomeNavKey.Home } },
                    onSelectProfile = profileViewModel::selectProfile,
                    onBarcodeScanner = { showBarcodeScanner.value = true },
                    onMenu = { scope.launch { railState.expand() } },
                    modifier = Modifier.focusRequester(focusRequester),
                )
            },
        ) { contentPadding ->
            val entries =
                rememberDecoratedNavEntries(
                    backStack = backStack,
                    entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                    entryProvider =
                        entryProvider {
                            entry<HomeNavKey.Home>(metadata = crossfadeTransitionMetadata) {
                                val homeState = rememberHomeState(LocalDate.now())

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = contentPadding.add(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    item {
                                        CalendarCard(
                                            homeState = homeState,
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                        )
                                    }
                                }
                            }
                            entry<HomeNavKey.Search>(metadata = transitionSearchMetadata) {
                                HomeSearchScreen(
                                    contentPadding =
                                        contentPadding.add(
                                            top =
                                                density.run { filterChipsHeight.floatValue.toDp() }
                                        ),
                                    selectedCollection = selectedCollection.value,
                                    onFoodDataCentralProduct = onFoodDataCentralProduct,
                                    onOpenFoodFactsProduct = onOpenFoodFactsProduct,
                                    onUserProduct = onUserProduct,
                                    onUserRecipe = onUserRecipe,
                                    lazyListState = lazyListState,
                                    onSearch = {
                                        searchViewModel.search(it)
                                        textFieldState.setTextAndPlaceCursorAtEnd(it)
                                        if (backStack.last() !is HomeNavKey.Search)
                                            backStack.add(HomeNavKey.Search)
                                    },
                                    onFill = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        },
                )

            val sceneState =
                rememberSceneState(
                    entries = entries,
                    sceneStrategies = listOf(SinglePaneSceneStrategy()),
                    onBack = { backStack.safeRemoveLast() },
                )

            val navigationEventState =
                rememberNavigationEventState(
                    currentInfo = SceneInfo(sceneState.currentScene),
                    backInfo = sceneState.previousScenes.map(::SceneInfo),
                )

            val animationSpec = motionScheme.fastSpatialSpec<Float>()
            NavigationBackHandler(
                state = navigationEventState,
                isBackEnabled =
                    sceneState.currentScene.previousEntries.isNotEmpty() && !fabExpanded,
                onBackCancelled = { scope.launch { backProgress.animateTo(0f, animationSpec) } },
                onBackCompleted = {
                    scope.launch {
                        backProgress.animateTo(1f, animationSpec) { scope.launch { snapTo(0f) } }
                    }
                    backStack.removeLastIf { true }
                },
            )
            NavigationEventHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = fabExpanded,
                onBackCompleted = { fabExpanded = false },
            )

            LaunchedEffect(navigationEventState.transitionState) {
                val transitionState = navigationEventState.transitionState
                if (transitionState is NavigationEventTransitionState.InProgress) {
                    backProgress.snapTo(transitionState.latestEvent.progress)
                }
            }

            NavDisplay(sceneState = sceneState, navigationEventState = navigationEventState)

            if (!isHome) {
                Box(
                    Modifier.fillMaxWidth()
                        .padding(top = contentPadding.calculateTopPadding())
                        .graphicsLayer {
                            alpha =
                                when {
                                    isHome -> 0f
                                    backProgress.value != 0f ->
                                        (1 - backProgress.value / Crossfade.PROGRESS_THRESHOLD)

                                    else -> isSearch.value
                                }
                            translationY = (1 - isSearch.value) * size.height
                            filterChipsHeight.floatValue = size.height
                        }
                ) {
                    SearchFilters(
                        collections = collections,
                        selected = selectedCollection.value,
                        onCollection = {
                            selectedCollection.value = it
                            if (backStack.last() !is HomeNavKey.Search)
                                backStack.add(HomeNavKey.Search)
                        },
                        contentPadding =
                            PaddingValues(horizontal = 16.dp) +
                                WindowInsets.statusBars.asPaddingValues().horizontal() +
                                WindowInsets.displayCutout.asPaddingValues().horizontal(),
                    )
                }
            }
        }
    }

    // TODO: Navigation rail is invisible on first launch when dynamic colors are enabled. Requires
    //       activity recreation to render correctly (e.g. theme change or screen rotation).
    HomeModalWideNavigationRail(
        state = railState,
        collections = collections,
        onCollection = {
            val selected = selectedCollection.value

            if (selected == null || it::class != selected::class) {
                selectedCollection.value = it
            }

            if (backStack.last() !is HomeNavKey.Search) backStack.add(HomeNavKey.Search)
            scope.launch { railState.collapse() }
        },
        onCreate = {
            scope.launch { railState.collapse() }
            onCreateProduct()
        },
    )
    StatusBarProtection { (offset.value / topBarHeight).coerceIn(0f, 1f) }
}

@Composable
private fun HomeModalWideNavigationRail(
    state: WideNavigationRailState,
    collections: List<CollectionFilter>,
    onCollection: (SearchCollection) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    ModalWideNavigationRail(
        modifier = modifier,
        header = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 24.dp),
            ) {
                IconButton(
                    onClick = { scope.launch { state.collapse() } },
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuOpen,
                        contentDescription = stringResource(Res.string.action_close),
                    )
                }
                FloatingActionButton(
                    onClick = onCreate,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(Res.string.action_create),
                    )
                }
            }
        },
        hideOnCollapse = true,
        expandedShape =
            MaterialTheme.shapes.large.copy(topStart = CornerSize(0), bottomStart = CornerSize(0)),
        state = state,
        contentPadding =
            PaddingValues(
                top = 8.dp,
                bottom = WideNavigationRailDefaults.ContentPadding.calculateBottomPadding(),
            ),
    ) {
        Text(
            modifier = Modifier.padding(start = 40.dp).padding(vertical = 16.dp),
            text = stringResource(Res.string.headline_collections),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        collections.forEach { collection ->
            WideNavigationRailItem(
                selected = false,
                onClick = { onCollection(collection.collection) },
                icon = { collection.collection.Icon(false, Modifier.size(24.dp)) },
                label = { Text(collection.collection.stringResource()) },
                railExpanded = true,
            )
        }
    }
}

@Immutable
@Serializable
private sealed interface HomeNavKey : NavKey {
    @Immutable @Serializable data object Home : HomeNavKey

    @Immutable @Serializable data object Search : HomeNavKey
}

@OptIn(ExperimentalSerializationApi::class)
private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) { subclassesOfSealed<HomeNavKey>() }
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
