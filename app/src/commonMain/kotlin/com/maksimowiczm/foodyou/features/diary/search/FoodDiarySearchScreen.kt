package com.maksimowiczm.foodyou.features.diary.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchFloatingActionButton
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.FoodSearchList
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchCollection
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchHints
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.favoritefood.FavoriteFoodSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.fooddatacentral.FoodDataCentralSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts.OpenFoodFactsSearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.rememberCollectionFilters
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood.UserFoodSearchExtension
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.Scrim
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.extension.plus
import com.maksimowiczm.foodyou.shared.ui.extension.toDp
import com.maksimowiczm.foodyou.shared.ui.saveable.jsonSaver
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodDiarySearchScreen(
    onBack: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    mealIdentity: MealIdentity,
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDiarySearchViewModel = koinViewModel { parametersOf(mealIdentity) }
    val meal = viewModel.meal.collectAsStateWithLifecycle().value

    val searchViewModel: SearchViewModel = koinViewModel()

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val shimmer = rememberShimmer(ShimmerBounds.View)

    val textFieldState = rememberTextFieldState()

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val collections = rememberCollectionFilters()
    var selectedCollection by
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

    val searchQuery = searchViewModel.searchQuery.collectAsStateWithLifecycle().value

    val openFoodFactsExtension = searchViewModel.extension<OpenFoodFactsSearchExtension>()
    val foodDataCentralExtension = searchViewModel.extension<FoodDataCentralSearchExtension>()
    val userFoodExtension = searchViewModel.extension<UserFoodSearchExtension>()
    val favoriteFoodExtension = searchViewModel.extension<FavoriteFoodSearchExtension>()

    LaunchedEffect(selectedCollection) {
        when (val collection = selectedCollection) {
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
        },
    )

    val searchBarState = rememberSearchBarState()
    val searchInputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                    searchViewModel.search(it)
                    textFieldState.setTextAndPlaceCursorAtEnd(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.action_search_foods),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                leadingIcon = {
                    if (searchBarState.targetValue == SearchBarValue.Expanded)
                        IconButton(
                            onClick = { scope.launch { searchBarState.animateToCollapsed() } },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(Res.string.action_go_back),
                            )
                        }
                    else
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                        )
                },
                trailingIcon = {
                    Row {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    textFieldState.clearText()
                                    searchViewModel.search(null)
                                },
                                shapes = IconButtonDefaults.shapes(),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(Res.string.action_clear),
                                )
                            }
                        }
                        IconButton(
                            onClick = { showBarcodeScanner.value = true },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_barcode_scanner),
                                contentDescription = stringResource(Res.string.action_scan_barcode),
                            )
                        }
                    }
                },
            )
        }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    val fabInsets =
        WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    val navigationEventState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationEventHandler(
        state = navigationEventState,
        isBackEnabled = fabExpanded,
        onBackCompleted = { fabExpanded = false },
    )

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
                    .animateFloatingActionButton(
                        visible = !LocalNavAnimatedContentScope.current.transition.isRunning,
                        alignment = Alignment.BottomEnd,
                    ),
        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f),
        )
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        if (meal == null)
                            Spacer(
                                Modifier.shimmer()
                                    .height(LocalTextStyle.current.toDp() - 4.dp)
                                    .padding(vertical = 2.dp)
                                    .width(100.dp)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            )
                        else Text(meal.name)
                    },
                    navigationIcon = { ArrowBackIconButton(onBack) },
                    subtitle = { Text(LocalDateFormatter.current.formatDate(date)) },
                    titleHorizontalAlignment = Alignment.CenterHorizontally,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { contentPadding ->
            FoodSearchList(
                selectedCollection = selectedCollection,
                userFood = userFoodExtension.pages.collectAsLazyPagingItems(),
                openFoodFacts = openFoodFactsExtension.pages.collectAsLazyPagingItems(),
                foodDataCentral = foodDataCentralExtension.pages.collectAsLazyPagingItems(),
                favoriteFood = favoriteFoodExtension.pages.collectAsLazyPagingItems(),
                searchQuery = searchQuery,
                contentPadding =
                    contentPadding.add(top = density.run { filterChipsHeight.floatValue.toDp() }),
                lazyListState = lazyListState,
                onFoodDataCentralProduct = onFoodDataCentralProduct,
                onOpenFoodFactsProduct = onOpenFoodFactsProduct,
                onUserProduct = onUserProduct,
                onUserRecipe = onUserRecipe,
                shimmer = shimmer,
                modifier = Modifier.fillMaxSize(),
            )
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(top = contentPadding.calculateTopPadding())
                        .graphicsLayer { filterChipsHeight.floatValue = size.height },
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                SearchBar(
                    state = searchBarState,
                    inputField = searchInputField,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
                SearchFilters(
                    collections = collections,
                    selected = selectedCollection,
                    onCollection = { selectedCollection = it },
                    contentPadding =
                        PaddingValues(horizontal = 16.dp) +
                            WindowInsets.statusBars.asPaddingValues().horizontal() +
                            WindowInsets.displayCutout.asPaddingValues().horizontal(),
                )
            }
        }
    }
    ExpandedFullScreenSearchBar(state = searchBarState, inputField = searchInputField) {
        SearchHints(
            history = searchViewModel.searchHistory.collectAsStateWithLifecycle().value,
            onSearch = {
                scope.launch { searchBarState.animateToCollapsed() }
                searchViewModel.search(it)
                textFieldState.setTextAndPlaceCursorAtEnd(it)
            },
            onFill = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
            shimmer = shimmer,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
