package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.app.navigation.Crossfade
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchFloatingActionButton
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchList
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchCollection
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.rememberCollectionFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.features.home.ui.calendar.CalendarCard
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.ui.component.ScrimWithPredictiveBack
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.extension.plus
import com.maksimowiczm.foodyou.shared.ui.saveable.jsonSaver
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(
    onAvatar: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    initialQuery: String?,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel { parametersOf(initialQuery) }
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val motionScheme = MaterialTheme.motionScheme
    val textFieldState = rememberTextFieldState()
    val focusRequester = remember { FocusRequester() }
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val railState = rememberWideNavigationRailState()

    val searchQuery = searchViewModel.searchQuery.collectAsStateWithLifecycle().value
    val history = searchViewModel.searchHistory.collectAsStateWithLifecycle().value

    val userFood =
        searchViewModel.extension<UserFoodSearchExtension>().pages.collectAsLazyPagingItems()
    val openFoodFacts =
        searchViewModel.extension<OpenFoodFactsSearchExtension>().pages.collectAsLazyPagingItems()
    val foodDataCentral =
        searchViewModel.extension<FoodDataCentralSearchExtension>().pages.collectAsLazyPagingItems()
    val favoriteFood =
        searchViewModel.extension<FavoriteFoodSearchExtension>().pages.collectAsLazyPagingItems()

    val showQuickResults = remember { mutableStateOf(searchQuery !is SearchQuery.Blank) }
    LaunchedEffect(searchQuery) {
        if (searchQuery is SearchQuery.Blank) {
            showQuickResults.value = false
        } else {
            delay(100.milliseconds)
            showQuickResults.value = true
        }
    }

    val isSearching = rememberSaveable { mutableStateOf(false) }
    val fabExpanded = rememberSaveable { mutableStateOf(false) }

    val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)

    // TODO
    //  This causes many recompositions, but we need this for searchbar predictive back animation
    val latestEvent =
        (navigationState.transitionState as? NavigationEventTransitionState.InProgress)?.latestEvent
    val homeProgressAnimatable = remember { Animatable(if (isSearching.value) 0f else 1f) }

    val openSearch: () -> Unit = {
        isSearching.value = true
        scope.launch {
            homeProgressAnimatable.animateTo(0f, motionScheme.fastSpatialSpec())
        }
    }
    val closeSearch: () -> Unit = {
        isSearching.value = false
        scope.launch {
            homeProgressAnimatable.animateTo(1f, motionScheme.fastSpatialSpec())
            viewModel.selectMeal(null)
        }
    }

    NavigationBackHandler(
        isBackEnabled = isSearching.value && !fabExpanded.value,
        state = navigationState,
        onBackCompleted = {
            isSearching.value = false
            scope.launch {
                if (latestEvent != null) homeProgressAnimatable.snapTo(latestEvent.progress)
                homeProgressAnimatable.animateTo(
                    1f,
                    motionScheme.fastSpatialSpec(),
                )
                viewModel.selectMeal(null)
            }
        },
    )

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val collections = rememberCollectionFilters()
    val selectedCollection =
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

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
            openSearch()
        },
    )

    Box {
        ScrimWithPredictiveBack(
            visible = fabExpanded.value,
            onDismiss = { fabExpanded.value = false },
            modifier = Modifier.fillMaxSize().zIndex(100f),
        )
        FoodSearchFloatingActionButton(
            fabExpanded = fabExpanded.value,
            onFabExpandedChange = { fabExpanded.value = it },
            onCreateRecipe = onCreateRecipe,
            onCreateProduct = onCreateProduct,
            modifier =
                Modifier.zIndex(200f)
                    .align(Alignment.BottomEnd)
                    .animateFloatingActionButton(
                        visible = isSearching.value && latestEvent == null,
                        alignment = Alignment.BottomEnd,
                    ),
        )
        Scaffold(
            modifier = modifier,
            topBar = {
                val homeProgress = { latestEvent?.progress ?: homeProgressAnimatable.value }
                val topBarHeight = remember { mutableIntStateOf(0) }

                val color = MaterialTheme.colorScheme.surface
                val brush = remember {
                    Brush.verticalGradient(
                        0f to color.copy(alpha = .9f),
                        0.6f to color.copy(alpha = .8f),
                        0.7f to color.copy(alpha = .6f),
                        0.9f to color.copy(alpha = .4f),
                        1f to Color.Transparent,
                    )
                }

                HomeScreenTopBar(
                    profile = uiState.selectedProfile,
                    profiles = uiState.profiles,
                    meal = uiState.activeMeal,
                    showMeal = isSearching.value && latestEvent == null,
                    date = uiState.date,
                    textFieldState = textFieldState,
                    shimmer = shimmer,
                    homeProgress = homeProgress,
                    onAvatar = onAvatar,
                    onSearch = searchViewModel::search,
                    onSearchBar = {
                        openSearch()
                        scope.launch {
                            delay(DefaultDurationMillis.milliseconds)
                            focusRequester.requestFocus()
                        }
                    },
                    onBack = closeSearch,
                    onSelectProfile = viewModel::selectProfile,
                    onBarcodeScanner = { showBarcodeScanner.value = true },
                    onMenu = { scope.launch { railState.expand() } },
                    modifier =
                        Modifier.zIndex(100f)
                            .pointerInput(Unit) { detectTapGestures {} }
                            .drawBehind {
                                drawRect(brush = brush, size = Size(size.width, size.height))
                            }
                            .focusRequester(focusRequester)
                            .onGloballyPositioned { topBarHeight.value = it.size.height },
                )
            },
        ) { contentPadding ->
            if (isSearching.value) {
                Box(
                    Modifier.fillMaxWidth()
                        .zIndex(100f)
                        .padding(top = contentPadding.calculateTopPadding() + 4.dp)
                        .graphicsLayer {
                            alpha =
                                when {
                                    !isSearching.value -> 0f
                                    latestEvent != null ->
                                        (1 - latestEvent.progress / Crossfade.PROGRESS_THRESHOLD)

                                    else -> 1 - homeProgressAnimatable.value
                                }
                            filterChipsHeight.floatValue = size.height
                        }
                ) {
                    SearchFilters(
                        collections = collections,
                        selected = selectedCollection.value,
                        onCollection = { selectedCollection.value = it },
                        contentPadding =
                            PaddingValues(horizontal = 16.dp) +
                                WindowInsets.statusBars.asPaddingValues().horizontal() +
                                WindowInsets.displayCutout.asPaddingValues().horizontal(),
                    )
                }
                FoodSearchList(
                    selectedCollection = selectedCollection.value,
                    userFood = userFood,
                    openFoodFacts = openFoodFacts,
                    foodDataCentral = foodDataCentral,
                    favoriteFood = favoriteFood,
                    searchQuery = searchQuery,
                    contentPadding =
                        contentPadding.add(
                            top =
                                LocalDensity.current.run { filterChipsHeight.floatValue.toDp() } +
                                    4.dp,
                            bottom = 80.dp,
                        ),
                    lazyListState = lazyListState,
                    onFoodDataCentralProduct = onFoodDataCentralProduct,
                    onOpenFoodFactsProduct = onOpenFoodFactsProduct,
                    onUserProduct = onUserProduct,
                    onUserRecipe = onUserRecipe,
                    shimmer = shimmer,
                    modifier =
                        Modifier.zIndex(50f).fillMaxSize().graphicsLayer {
                            alpha =
                                when {
                                    latestEvent != null ->
                                        1 -
                                            (latestEvent.progress /
                                                (Crossfade.PROGRESS_THRESHOLD / 2))

                                    else -> 1 - homeProgressAnimatable.value
                                }
                        },
                    history = history,
                    showQuickResults = showQuickResults.value,
                    onSearch = {
                        searchViewModel.search(it)
                        textFieldState.setTextAndPlaceCursorAtEnd(it)
                    },
                    onFill = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                )
            }
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize().graphicsLayer {
                        alpha =
                            when {
                                latestEvent != null ->
                                    (latestEvent.progress - Crossfade.PROGRESS_THRESHOLD / 2) /
                                        Crossfade.PROGRESS_THRESHOLD

                                else -> homeProgressAnimatable.value
                            }
                    },
                contentPadding = contentPadding.add(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    CalendarCard(
                        date = uiState.date,
                        onSelectDate = viewModel::selectDate,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    )
                }
                item {
                    MealCards(
                        meals = uiState.meals,
                        shimmer = shimmer,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onAdd = {
                            viewModel.selectMeal(it)
                            openSearch()
                        },
                        onEntry = {
                            // TODO
                        },
                    )
                }
            }
        }
    }

    // TODO: Navigation rail is invisible on first launch when dynamic colors are enabled. Requires
    //       activity recreation to render correctly (e.g. theme change or screen rotation).
    ModalWideNavigationRail(
        header = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 24.dp),
            ) {
                IconButton(
                    onClick = { scope.launch { railState.collapse() } },
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuOpen,
                        contentDescription = stringResource(Res.string.action_close),
                    )
                }
            }
        },
        hideOnCollapse = true,
        expandedShape =
            MaterialTheme.shapes.large.copy(topStart = CornerSize(0), bottomStart = CornerSize(0)),
        state = railState,
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
                onClick = {
                    val selected = selectedCollection.value

                    if (selected == null || collection::class != selected::class) {
                        selectedCollection.value = collection.collection
                    }

                    scope.launch { railState.collapse() }
                    openSearch()
                },
                icon = { collection.collection.Icon(false, Modifier.size(24.dp)) },
                label = { Text(collection.collection.stringResource()) },
                railExpanded = true,
            )
        }
    }
}
