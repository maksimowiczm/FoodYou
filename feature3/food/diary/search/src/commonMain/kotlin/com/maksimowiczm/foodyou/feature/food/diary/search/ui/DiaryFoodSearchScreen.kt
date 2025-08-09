package com.maksimowiczm.foodyou.feature.food.diary.search.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.error
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductError
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.externaldatabase.usda.USDAException
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.DiaryFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.FoodFilter
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.FoodSearchUiState
import com.maksimowiczm.foodyou.feature.food.shared.ui.usda.UsdaErrorCard
import com.maksimowiczm.foodyou.feature.shared.ui.FoodListItemSkeleton
import com.maksimowiczm.foodyou.shared.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.Scrim
import com.maksimowiczm.foodyou.shared.ui.ext.add
import com.maksimowiczm.foodyou.shared.ui.ext.toDp
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun DiaryFoodSearchScreen(
    onBack: () -> Unit,
    onCreateRecipe: () -> Unit,
    onCreateProduct: () -> Unit,
    onMeasure: (FoodId, Measurement) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    date: LocalDate,
    mealId: Long,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current

    val viewModel: DiaryFoodSearchViewModel = koinViewModel { parametersOf(mealId) }
    val meal = viewModel.meal.collectAsStateWithLifecycle().value

    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabExpanded) { fabExpanded = false }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val topBar =
        @Composable {
            TopAppBar(
                title = {
                    updateTransition(meal).Crossfade(contentKey = { it?.toString() }) {
                        if (meal == null) {
                            Spacer(
                                modifier =
                                    Modifier.height(LocalTextStyle.current.toDp() - 4.dp)
                                        .width(100.dp)
                                        .padding(bottom = 4.dp)
                                        .shimmer()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        )
                            )
                        } else {
                            Text(meal.name)
                        }
                    }
                },
                subtitle = { Text(dateFormatter.formatDate(date)) },
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        }
    val content: @Composable (PaddingValues) -> Unit =
        @Composable { paddingValues ->
            DiaryFoodSearchScreen(
                uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                onSearch = viewModel::search,
                onSourceChange = viewModel::changeSource,
                onFoodClick = { food, measurement -> onMeasure(food.id, measurement) },
                onUpdateUsdaApiKey = onUpdateUsdaApiKey,
                modifier =
                    Modifier.padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
            )
        }

    Box(modifier) {
        val fabInsets =
            WindowInsets.systemBars
                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

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
                        visible = !animatedVisibilityScope.transition.isRunning,
                        alignment = Alignment.BottomEnd,
                    ),
        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f),
        )
        Scaffold(
            topBar = topBar,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
            floatingActionButton = {
                Box(modifier = Modifier.windowInsetsPadding(fabInsets).height(56.dp))
            },
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DiaryFoodSearchScreen(
    uiState: FoodSearchUiState,
    onSearch: (String?) -> Unit,
    onSourceChange: (FoodFilter.Source) -> Unit,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
    appState: FoodSearchAppState = rememberFoodSearchAppState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val onSearch: (String?) -> Unit =
        remember(onSearch, appState, coroutineScope) {
            { query ->
                appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(query ?: "")
                onSearch(query)
                coroutineScope.launch { appState.searchBarState.animateToCollapsed() }
            }
        }

    val pages = uiState.currentSourceState?.collectAsLazyPagingItems()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    FullScreenCameraBarcodeScanner(
        visible = appState.showBarcodeScanner,
        onBarcodeScan = {
            appState.showBarcodeScanner = false
            onSearch(it)
        },
        onClose = { appState.showBarcodeScanner = false },
    )

    val searchInputField =
        @Composable {
            FoodSearchBarInputField(
                searchBarState = appState.searchBarState,
                textFieldState = appState.searchTextFieldState,
                onSearch = onSearch,
                onBarcodeScanner = { appState.showBarcodeScanner = true },
            )
        }

    FoodSearchView(
        appState = appState,
        uiState = uiState,
        onFill = { search -> appState.searchTextFieldState.setTextAndPlaceCursorAtEnd(search) },
        onSearch = onSearch,
        onSource = onSourceChange,
        inputField = searchInputField,
    )

    Scaffold(modifier) { paddingValues ->
        // Fix for searchbar issues on Android SDK 27 and below
        Box(Modifier.focusable().size(1.dp))

        var topContentHeight by remember { mutableIntStateOf(0) }

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .zIndex(10f)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    )
                    .padding(top = paddingValues.calculateTopPadding())
                    .onSizeChanged { topContentHeight = it.height }
                    .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                state = appState.searchBarState,
                inputField = searchInputField,
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors =
                    SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                shadowElevation = 2.dp,
            )

            if (uiState.sources.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FoodSearchFilters(
                    uiState = uiState,
                    onSource = onSourceChange,
                    modifier = Modifier.height(32.dp + 8.dp + 32.dp).fillMaxWidth(),
                )
            }

            when (val ex = pages?.loadState?.error) {
                null -> Unit

                // This is really stupid but Paging 3 library exposes the exception from remote
                // mediator and we can't know what it is if we don't check it. For now this is
                // a "temporary" solution because fixing this would require rethinking how the
                // remote food search works in business module.
                is USDAException ->
                    UsdaErrorCard(
                        error = ex.toUsdaError(),
                        onUpdateApiKey = onUpdateUsdaApiKey,
                        modifier =
                            Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 16.dp),
                    )

                else ->
                    FoodSearchErrorCard(
                        message = ex.message ?: stringResource(Res.string.error_unknown_error),
                        onRetry = pages::retry,
                        modifier =
                            Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 16.dp),
                    )
            }
        }

        val paddingValues =
            paddingValues.add(
                top = LocalDensity.current.run { topContentHeight.toDp() },
                bottom = 56.dp + 32.dp,
            )

        Box(Modifier.fillMaxSize().zIndex(20f)) {
            if (pages?.itemCount == 0 && pages.loadState.append !is LoadState.Loading) {
                Text(
                    text = stringResource(Res.string.neutral_no_food_found),
                    modifier = Modifier.safeContentPadding().align(Alignment.Center),
                )
            }

            if (pages?.delayedLoadingState() == true) {
                ContainedLoadingIndicator(
                    modifier =
                        Modifier.align(Alignment.TopCenter)
                            .padding(top = paddingValues.calculateTopPadding())
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = paddingValues) {
            if (pages != null) {
                items(
                    count = pages.itemCount,
                    key = pages.itemKey { (it.id to uiState.filter.source).toString() },
                ) { i ->
                    val food = pages[i]

                    when (food) {
                        null -> FoodListItemSkeleton(shimmer)
                        is FoodSearch.Product -> {
                            val measurement = food.defaultMeasurement
                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                onClick = { onFoodClick(food, measurement) },
                            )
                        }

                        is FoodSearch.Recipe -> {
                            val measurement = food.defaultMeasurement
                            FoodSearchListItem(
                                food = food,
                                measurement = measurement,
                                onClick = { onFoodClick(food, measurement) },
                                shimmer = shimmer,
                            )
                        }
                    }
                }

                if (pages.loadState.append is LoadState.Loading) {
                    items(10) { FoodListItemSkeleton(shimmer) }
                }
            }

            if (pages == null) {
                items(10) { FoodListItemSkeleton(shimmer) }
            }
        }
    }
}

private fun USDAException.toUsdaError(): DownloadProductError.Usda =
    when (this) {
        is USDAException.ApiKeyDisabledException,
        is USDAException.ApiKeyInvalidException,
        is USDAException.ApiKeyIsMissingException,
        is USDAException.ApiKeyUnauthorizedException,
        is USDAException.ProductNotFoundException -> DownloadProductError.Usda.ApiKeyInvalid

        is USDAException.ApiKeyUnverifiedException -> DownloadProductError.Usda.ApiKeyUnverified
        is USDAException.RateLimitException -> DownloadProductError.Usda.RateLimit
    }
