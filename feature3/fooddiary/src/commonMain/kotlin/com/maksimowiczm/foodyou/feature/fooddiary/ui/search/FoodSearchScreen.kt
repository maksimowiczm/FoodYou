package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.error
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Food
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsPrivacyDialog
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsSearchList
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalAnimationApi::class
)
@Composable
internal fun FoodSearchScreen(
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProduct) -> Unit,
    onFood: (Food, Measurement) -> Unit,
    viewModel: FoodSearchViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    useOpenFoodFactsPreference: UseOpenFoodFacts = userPreference()
) {
    val localPages = viewModel.localPages.collectAsLazyPagingItems()
    val openFoodFactsPages = viewModel.openFoodFactsPages.collectAsLazyPagingItems()
    val meal = viewModel.meal.collectAsStateWithLifecycle().value
    val date = viewModel.date.collectAsStateWithLifecycle().value
    val useOpenFoodFacts = useOpenFoodFactsPreference
        .collectAsStateWithLifecycle(useOpenFoodFactsPreference.getBlocking()).value

    val dateFormatter = LocalDateFormatter.current
    val coroutineScope = rememberCoroutineScope()

    val navController = rememberNavController()
    val backstack = navController.currentBackStackEntryFlow
        .collectAsStateWithLifecycle(null).value
    val state = backstack?.destination?.route?.let { FoodSearchState.valueOf(it) }
    val handleBack: () -> Unit = {
        when (backstack?.destination?.route) {
            FoodSearchState.Local.name -> onBack()
            FoodSearchState.OpenFoodFacts.name -> navController.popBackStack(
                route = FoodSearchState.OpenFoodFacts.name,
                inclusive = true
            )

            else -> error("Unknown backstack state: ${backstack?.destination?.route}")
        }
    }

    var showOpenFoodFactsPrivacyDialog by rememberSaveable { mutableStateOf(false) }
    if (showOpenFoodFactsPrivacyDialog) {
        OpenFoodFactsPrivacyDialog(
            onDismissRequest = { showOpenFoodFactsPrivacyDialog = false },
            onConfirm = {
                showOpenFoodFactsPrivacyDialog = false
                coroutineScope.launch {
                    useOpenFoodFactsPreference.set(true)
                }
            }
        )
    }

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    val searchState = rememberSearchBarState()
    val searchTextFieldState = rememberTextFieldState()
    val inputField = @Composable {
        SearchBarInputField(
            searchState = state ?: FoodSearchState.Local,
            searchBarState = searchState,
            textFieldState = searchTextFieldState,
            onSearch = {
                viewModel.search(it)
                coroutineScope.launch {
                    searchState.animateToCollapsed()
                }
            },
            onBarcodeScanner = { showBarcodeScanner = true }
        )
    }

    ExpandedFullScreenSearchBar(
        state = searchState,
        inputField = inputField
    ) {
    }

    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner,
        onBarcodeScan = {
            viewModel.search(it)
            searchTextFieldState.setTextAndPlaceCursorAtEnd(it)
            showBarcodeScanner = false
            coroutineScope.launch {
                searchState.animateToCollapsed()
            }
        },
        onClose = { showBarcodeScanner = false }
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    updateTransition(meal).Crossfade(
                        contentKey = { it?.toString() }
                    ) {
                        if (meal == null) {
                            Spacer(
                                modifier = Modifier
                                    .height(LocalTextStyle.current.toDp() - 4.dp)
                                    .width(100.dp)
                                    .padding(bottom = 4.dp)
                                    .shimmer()
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            )
                        } else {
                            Text(meal.name)
                        }
                    }
                },
                subtitle = {
                    Text(dateFormatter.formatDate(date))
                },
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                navigationIcon = {
                    ArrowBackIconButton(handleBack)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val contentPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection),
            end = paddingValues.calculateEndPadding(layoutDirection),
            bottom = paddingValues.calculateBottomPadding()
        )

        var searchBarHeight by remember { mutableIntStateOf(0) }
        var errorCardHeight by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                state = searchState,
                inputField = inputField,
                modifier = Modifier
                    .onSizeChanged { searchBarHeight = it.height }
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shadowElevation = 2.dp
            )

            when (state) {
                null -> Unit

                FoodSearchState.Local -> {
                    if (!localPages.loadState.isIdle) {
                        ContainedLoadingIndicator()
                    }
                }

                FoodSearchState.OpenFoodFacts -> {
                    val error = openFoodFactsPages.loadState.error
                    val message = error?.message ?: stringResource(Res.string.error_unknown_error)

                    if (error != null) {
                        ErrorCard(
                            message = message,
                            onRetry = { openFoodFactsPages.retry() },
                            modifier = Modifier
                                .onSizeChanged { errorCardHeight = it.height }
                                .padding(horizontal = 16.dp)
                        )
                    } else if (!openFoodFactsPages.loadState.isIdle) {
                        ContainedLoadingIndicator()
                    }
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = FoodSearchState.Local.name
        ) {
            forwardBackwardComposable(FoodSearchState.Local.name) {
                val measurements = viewModel.measurements
                    .collectAsStateWithLifecycle().value
                val openFoodFactsCount = viewModel.openFoodFactsProductCount
                    .collectAsStateWithLifecycle().value

                LocalSearchList(
                    pages = localPages,
                    measurements = measurements,
                    onCreateProduct = onCreateProduct,
                    onMeasurement = { food, measurement ->
                        if (meal != null) {
                            viewModel.measureFood(food, measurement, meal, date)
                        }
                    },
                    onDeleteMeasurement = { measurementId ->
                        viewModel.deleteMeasurement(measurementId)
                    },
                    onFoodClick = onFood,
                    useOpenFoodFacts = useOpenFoodFacts,
                    openFoodFactsCount = openFoodFactsCount,
                    openFoodFactsLoadState = openFoodFactsPages.loadState,
                    onOpenFoodFactsPrivacyDialog = {
                        showOpenFoodFactsPrivacyDialog = true
                    },
                    onOpenFoodFacts = {
                        navController.navigate(FoodSearchState.OpenFoodFacts.name) {
                            launchSingleTop = true
                        }
                    },
                    contentPadding = contentPadding.add(
                        top = LocalDensity.current.run { searchBarHeight.toDp() }
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.fillMaxSize()
                )
            }

            forwardBackwardComposable(FoodSearchState.OpenFoodFacts.name) {
                OpenFoodFactsSearchList(
                    pages = openFoodFactsPages,
                    contentPadding = contentPadding.add(
                        top = LocalDensity.current.run {
                            val errorHeight = if (openFoodFactsPages.loadState.error != null) {
                                errorCardHeight.toDp() + 8.dp
                            } else {
                                0.dp
                            }

                            searchBarHeight.toDp() + errorHeight
                        }
                    ),
                    onClick = onOpenFoodFactsProduct,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInputField(
    searchState: FoodSearchState,
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
    onSearch: (String?) -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = onSearch,
        modifier = modifier,
        placeholder = {
            when (searchState) {
                FoodSearchState.Local -> Text(stringResource(Res.string.action_search))
                FoodSearchState.OpenFoodFacts -> Text(
                    stringResource(Res.string.headline_browse_open_food_facts)
                )
            }
        },
        leadingIcon = {
            if (searchBarState.targetValue == SearchBarValue.Expanded) {
                ArrowBackIconButton(
                    onClick = {
                        coroutineScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    }
                )
            } else {
                when (searchState) {
                    FoodSearchState.Local -> Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )

                    FoodSearchState.OpenFoodFacts -> Image(
                        painter = painterResource(Res.drawable.openfoodfacts_logo),
                        contentDescription = null,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        },
        trailingIcon = {
            if (textFieldState.text.isEmpty()) {
                IconButton(
                    onClick = onBarcodeScanner
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_barcode_scanner),
                        contentDescription = stringResource(Res.string.action_scan_barcode)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd("")
                        if (searchBarState.targetValue == SearchBarValue.Collapsed) {
                            onSearch(null)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = stringResource(Res.string.action_clear)
                    )
                }
            }
        }
    )
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    var showDetails by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.neutral_an_error_occurred),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.neutral_open_food_facts_error),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        showDetails = !showDetails
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_show_details))
                }

                FilledTonalButton(
                    onClick = {
                        onRetry()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.errorContainer,
                        containerColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(showDetails) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
