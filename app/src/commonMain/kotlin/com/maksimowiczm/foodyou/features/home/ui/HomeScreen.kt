package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.app.navigation.Crossfade
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.CollectionFilter
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchFloatingActionButton
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchList
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchCollection
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.rememberCollectionFilter
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.rememberCollectionFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.features.home.ui.calendar.CalendarCard
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.shared.ui.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreenContent
import com.maksimowiczm.foodyou.shared.ui.component.ScrimWithPredictiveBack
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.extension.plus
import com.maksimowiczm.foodyou.shared.ui.saveable.jsonSaver
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(
    onAvatar: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductId, Quantity, MealId?, LocalDate?) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductId, Quantity, MealId?, LocalDate?) -> Unit,
    onUserProduct: (UserProductId, Quantity, MealId?, LocalDate?) -> Unit,
    onUserRecipe: (UserRecipeId, Quantity, MealId?, LocalDate?) -> Unit,
    onCreateProduct: (MealId?, LocalDate?) -> Unit,
    onCreateRecipe: (MealId?, LocalDate?) -> Unit,
    onEntry: (FoodDiaryEntryId) -> Unit,
    initialQuery: String?,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel { parametersOf(initialQuery) }
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()

    val userFood =
        searchViewModel.extension<UserFoodSearchExtension>().pages.collectAsLazyPagingItems()
    val openFoodFacts =
        searchViewModel.extension<OpenFoodFactsSearchExtension>().pages.collectAsLazyPagingItems()
    val foodDataCentral =
        searchViewModel.extension<FoodDataCentralSearchExtension>().pages.collectAsLazyPagingItems()
    val favoriteFood =
        searchViewModel.extension<FavoriteFoodSearchExtension>().pages.collectAsLazyPagingItems()

    val foodDataCentralExtension = searchViewModel.extension<FoodDataCentralSearchExtension>()
    val openFoodFactsExtension = searchViewModel.extension<OpenFoodFactsSearchExtension>()

    val collections = rememberCollectionFilters()

    HomeScreenContent(
        initialQuery = initialQuery,
        uiState = uiState,
        searchQuery = searchQuery,
        searchHistory = searchHistory,
        collections = collections,
        userFood = userFood,
        openFoodFacts = openFoodFacts,
        foodDataCentral = foodDataCentral,
        favoriteFood = favoriteFood,
        onAvatar = onAvatar,
        onSearch = searchViewModel::search,
        onSelectProfile = viewModel::selectProfile,
        onSelectDate = viewModel::selectDate,
        onSelectMeal = viewModel::selectMeal,
        onFoodDataCentralProduct = { product, quantity ->
            onFoodDataCentralProduct(product, quantity, uiState.activeMeal?.id, uiState.date)
        },
        onOpenFoodFactsProduct = { product, quantity ->
            onOpenFoodFactsProduct(product, quantity, uiState.activeMeal?.id, uiState.date)
        },
        onUserProduct = { product, quantity ->
            onUserProduct(product, quantity, uiState.activeMeal?.id, uiState.date)
        },
        onUserRecipe = { recipe, quantity ->
            onUserRecipe(recipe, quantity, uiState.activeMeal?.id, uiState.date)
        },
        onCreateProduct = { onCreateProduct(uiState.activeMeal?.id, uiState.date) },
        onCreateRecipe = { onCreateRecipe(uiState.activeMeal?.id, uiState.date) },
        onCollectionSelected = { collection ->
            when (collection) {
                is SearchCollection.FoodDataCentral ->
                    foodDataCentralExtension.dataTypes(collection.dataTypes)

                is SearchCollection.OpenFoodFacts ->
                    openFoodFactsExtension.version(collection.version)

                else -> Unit
            }
        },
        onEntry = onEntry,
        onDeleteEntry = viewModel::deleteDiaryEntry,
        modifier = modifier,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenContent(
    initialQuery: String?,
    uiState: HomeUiState,
    searchQuery: SearchQuery,
    searchHistory: List<String>?,
    collections: List<CollectionFilter>,
    userFood: LazyPagingItems<SearchResult>,
    openFoodFacts: LazyPagingItems<OpenFoodFactsProduct>,
    foodDataCentral: LazyPagingItems<FoodDataCentralProduct>,
    favoriteFood: LazyPagingItems<RemoteData<Any>>,
    onAvatar: () -> Unit,
    onSearch: (String?) -> Unit,
    onSelectProfile: (ProfileId) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectMeal: (MealId?) -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductId, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductId, Quantity) -> Unit,
    onUserProduct: (UserProductId, Quantity) -> Unit,
    onUserRecipe: (UserRecipeId, Quantity) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    onCollectionSelected: (SearchCollection) -> Unit,
    onEntry: (FoodDiaryEntryId) -> Unit,
    onDeleteEntry: (FoodDiaryEntryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val motionScheme = MaterialTheme.motionScheme
    val textFieldState = rememberTextFieldState(initialQuery ?: "")
    val focusRequester = remember { FocusRequester() }
    val shimmer = rememberShimmer(ShimmerBounds.View)
    val railState = rememberWideNavigationRailState()

    val showQuickResults = remember { mutableStateOf(searchQuery !is SearchQuery.Blank) }
    LaunchedEffect(searchQuery) {
        if (searchQuery is SearchQuery.Blank) {
            showQuickResults.value = false
        } else {
            delay(100.milliseconds)
            showQuickResults.value = true
        }
    }

    val isSearching = rememberSaveable(initialQuery) { mutableStateOf(initialQuery != null) }
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
            onSelectMeal(null)
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
                onSelectMeal(null)
            }
        },
    )

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val selectedCollection =
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(selectedCollection.value) {
        selectedCollection.value?.let { onCollectionSelected(it) }
    }

    val showBarcodeScanner = rememberSaveable { mutableStateOf(false) }
    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner.value,
        onClose = { showBarcodeScanner.value = false },
        onBarcodeScan = {
            showBarcodeScanner.value = false
            onSearch(it)
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
                val brush =
                    remember(color) {
                        Brush.verticalGradient(
                            0f to color.copy(alpha = .9f),
                            0.6f to color.copy(alpha = .8f),
                            0.7f to color.copy(alpha = .6f),
                            0.9f to color.copy(alpha = .4f),
                            1f to Color.Transparent,
                        )
                    }

                Box(
                    Modifier.zIndex(100f)
                        .fillMaxWidth()
                        .pointerInput(Unit) { detectTapGestures {} }
                        .drawBehind {
                            drawRect(brush = brush, size = Size(size.width, size.height))
                        }
                ) {
                    HomeScreenTopBar(
                        profile = uiState.selectedProfile,
                        profiles = uiState.profiles,
                        meals = uiState.meals.filterIsInstance<HomeMealState.Linked>(),
                        activeMeal = uiState.activeMeal,
                        date = uiState.date,
                        textFieldState = textFieldState,
                        shimmer = shimmer,
                        homeProgress = homeProgress,
                        onAvatar = onAvatar,
                        onSearch = onSearch,
                        onSearchBar = {
                            openSearch()
                            scope.launch {
                                delay(DefaultDurationMillis.milliseconds)
                                focusRequester.requestFocus()
                            }
                        },
                        onBack = closeSearch,
                        onSelectProfile = onSelectProfile,
                        onBarcodeScanner = { showBarcodeScanner.value = true },
                        onMenu = { scope.launch { railState.expand() } },
                        onMeal = { onSelectMeal(it.id) },
                        onSelectDate = onSelectDate,
                        modifier =
                            Modifier.focusRequester(focusRequester).onGloballyPositioned {
                                topBarHeight.value = it.size.height
                            },
                    )
                }
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
                    history = searchHistory,
                    showQuickResults = showQuickResults.value,
                    onSearch = {
                        onSearch(it)
                        textFieldState.setTextAndPlaceCursorAtEnd(it)
                    },
                    onFill = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
                )
            }
            if (homeProgressAnimatable.value != 0f || latestEvent != null)
                updateTransition(uiState.selectedProfile, "selected profile").Crossfade(
                    contentKey = { it != null }
                ) {
                    if (it == null) LoadingScreenContent(Modifier.fillMaxSize())
                    else
                        LazyColumn(
                            modifier =
                                Modifier.fillMaxSize().graphicsLayer {
                                    alpha =
                                        when {
                                            latestEvent != null ->
                                                (latestEvent.progress -
                                                    Crossfade.PROGRESS_THRESHOLD / 2) /
                                                    Crossfade.PROGRESS_THRESHOLD

                                            else -> homeProgressAnimatable.value
                                        }
                                },
                            contentPadding = contentPadding.add(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            uiState.homeCardsOrder.forEach {
                                when (it) {
                                    HomeCard.Calendar ->
                                        item {
                                            CalendarCard(
                                                date = uiState.date,
                                                onSelectDate = onSelectDate,
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                            )
                                        }

                                    HomeCard.MealCards ->
                                        item {
                                            MealCards(
                                                meals = uiState.meals,
                                                shimmer = shimmer,
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                onAdd = { id ->
                                                    onSelectMeal(id)
                                                    openSearch()
                                                },
                                                onEntry = onEntry,
                                                onDeleteEntry = onDeleteEntry,
                                            )
                                        }
                                }
                            }
                        }
                }
        }
    }

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

@Preview
@Composable
private fun HomeScreenPreview() {
    val userFood = remember {
        flowOf(PagingData.from(emptyList<SearchResult>()))
    }
        .collectAsLazyPagingItems()
    val openFoodFacts = remember {
        flowOf(PagingData.from(emptyList<OpenFoodFactsProduct>()))
    }
        .collectAsLazyPagingItems()
    val foodDataCentral = remember {
        flowOf(PagingData.from(emptyList<FoodDataCentralProduct>()))
    }
        .collectAsLazyPagingItems()
    val favoriteFood = remember {
        flowOf(PagingData.from(emptyList<RemoteData<Any>>()))
    }
        .collectAsLazyPagingItems()
    val searchHistory = remember { listOf("Apple", "Pen") }
    val collections =
        listOf(
            rememberCollectionFilter(
                collection = SearchCollection.Favorite(),
                count = favoriteFood.itemCount,
                pages = favoriteFood,
            ),
            rememberCollectionFilter(
                collection = SearchCollection.UserFood(),
                count = userFood.itemCount,
                pages = userFood,
            ),
            rememberCollectionFilter(
                collection = SearchCollection.OpenFoodFacts(),
                count = openFoodFacts.itemCount,
                pages = openFoodFacts,
            ),
            rememberCollectionFilter(
                collection = SearchCollection.FoodDataCentral(),
                count = foodDataCentral.itemCount,
                pages = foodDataCentral,
            ),
        )

    PreviewFoodYouTheme {
        HomeScreenContent(
            initialQuery = null,
            uiState = HomeUiStateProvider().uiState,
            searchQuery = SearchQuery.Blank,
            searchHistory = searchHistory,
            collections = collections,
            userFood = userFood,
            openFoodFacts = openFoodFacts,
            foodDataCentral = foodDataCentral,
            favoriteFood = favoriteFood,
            onAvatar = {},
            onSearch = {},
            onSelectProfile = {},
            onSelectDate = {},
            onSelectMeal = {},
            onFoodDataCentralProduct = { _, _ -> },
            onOpenFoodFactsProduct = { _, _ -> },
            onUserProduct = { _, _ -> },
            onUserRecipe = { _, _ -> },
            onCreateProduct = {},
            onCreateRecipe = {},
            onCollectionSelected = {},
            onEntry = {},
            onDeleteEntry = {},
        )
    }
}
