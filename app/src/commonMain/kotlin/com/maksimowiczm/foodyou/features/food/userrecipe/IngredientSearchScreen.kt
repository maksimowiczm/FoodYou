package com.maksimowiczm.foodyou.features.food.userrecipe

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
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
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.shared.ui.component.StatusBarProtection
import com.maksimowiczm.foodyou.shared.ui.component.StatusBarProtectionDefaults.rememberScrollConnection
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun IngredientSearchScreen(
    recipeIdentity: UserRecipeIdentity?,
    onBack: () -> Unit,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity, Quantity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity, Quantity) -> Unit,
    onUserProduct: (UserProductIdentity, Quantity) -> Unit,
    onUserRecipe: (UserRecipeIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = koinViewModel { parametersOf(recipeIdentity) }

    val density = LocalDensity.current

    val shimmer = rememberShimmer(ShimmerBounds.View)

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val topBarHeight = density.run { 64.dp.toPx() }
    val scrollConnection = rememberScrollConnection { offset.floatValue -= it.y }

    val textFieldState = rememberTextFieldState()

    val filterChipsHeight = remember { mutableFloatStateOf(0f) }
    val collections = rememberCollectionFilters()
    var selectedCollection by
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<SearchCollection?>(null) }
    val lazyListState = rememberLazyListState()

    val searchQuery = searchViewModel.searchQuery.collectAsStateWithLifecycle().value
    val showQuickResults = remember { mutableStateOf(searchQuery !is SearchQuery.Blank) }
    LaunchedEffect(searchQuery) {
        if (searchQuery is SearchQuery.Blank) {
            showQuickResults.value = false
        } else {
            delay(100.milliseconds)
            showQuickResults.value = true
        }
    }

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

    Scaffold(
        modifier = modifier.nestedScroll(scrollConnection),
        topBar = {
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                        .height(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                SearchBarContent(
                    textFieldState = textFieldState,
                    onBack = onBack,
                    onSearch = {
                        searchViewModel.search(it)
                        textFieldState.setTextAndPlaceCursorAtEnd(it)
                    },
                    onClear = {
                        searchViewModel.search(null)
                        textFieldState.clearText()
                    },
                    onBarcodeScanner = {
                        showBarcodeScanner.value = true
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
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
            history = searchViewModel.searchHistory.collectAsStateWithLifecycle().value,
            showQuickResults = showQuickResults.value,
            onSearch = {
                searchViewModel.search(it)
                textFieldState.setTextAndPlaceCursorAtEnd(it)
            },
            onFill = {
                textFieldState.setTextAndPlaceCursorAtEnd(it)
            },
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            Modifier.fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding())
                .graphicsLayer { filterChipsHeight.floatValue = size.height }
        ) {
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

        StatusBarProtection { (offset.value / topBarHeight).coerceIn(0f, 1f) }
    }
}

@Composable
private fun SearchBarContent(
    textFieldState: TextFieldState,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SearchBarDefaults.inputFieldColors()

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(
            Modifier.sizeIn(
                    minWidth = 360.dp,
                    maxWidth = 720.dp,
                    minHeight = SearchBarDefaults.InputFieldHeight,
                    maxHeight = SearchBarDefaults.InputFieldHeight,
                )
                .padding(horizontal = 4.dp)
        ) {
            BasicTextField(
                state = textFieldState,
                modifier =
                    modifier.sizeIn(minHeight = SearchBarDefaults.InputFieldHeight).fillMaxWidth(),
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle =
                    MaterialTheme.typography.bodyLarge.merge(MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(colors.cursorColor(isError = false)),
                keyboardOptions =
                    KeyboardOptions.Default.merge(KeyboardOptions(imeAction = ImeAction.Search)),
                onKeyboardAction = { onSearch(textFieldState.text.toString()) },
                interactionSource = interactionSource,
                decorator =
                    TextFieldDefaults.decorator(
                        state = textFieldState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        enabled = true,
                        outputTransformation = null,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = PaddingValues(start = 44.dp, end = 92.dp),
                        container = { Box(Modifier) },
                    ),
            )
            IconButton(
                onClick = onBack,
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = stringResource(Res.string.action_close),
                )
            }
            if (textFieldState.text.isEmpty()) {
                Text(
                    text = stringResource(Res.string.headline_search_ingredients),
                    modifier = Modifier.wrapContentSize().align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(Res.string.action_clear),
                        )
                    }
                }
                IconButton(onClick = onBarcodeScanner, shapes = IconButtonDefaults.shapes()) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_barcode_scanner),
                        contentDescription = stringResource(Res.string.action_scan_barcode),
                    )
                }
            }
        }
    }
}
