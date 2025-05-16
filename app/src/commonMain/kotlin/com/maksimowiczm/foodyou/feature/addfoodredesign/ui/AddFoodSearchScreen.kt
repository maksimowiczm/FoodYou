package com.maksimowiczm.foodyou.feature.addfoodredesign.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddFoodSearchScreen(
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (fabExpanded) .5f else 0f
    )

    BackHandler(fabExpanded) { fabExpanded = false }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Fab(
                expanded = fabExpanded,
                onExpandedChange = { expanded ->
                    fabExpanded = expanded
                },
                onRecipeAdd = {
                    onRecipeAdd()
                    fabExpanded = false
                },
                onProductAdd = {
                    onProductAdd()
                    fabExpanded = false
                }
            )
        }
    ) {
        if (fabExpanded) {
            Spacer(
                Modifier
                    .fillMaxSize()
                    .zIndex(10f)
                    .graphicsLayer { alpha = scrimAlpha }
                    .pointerInput(Unit) { detectTapGestures { fabExpanded = false } }
                    .background(MaterialTheme.colorScheme.scrim)
            )
        }

        Content(
            onBack = {},
            onSearch = {}
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Fab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRecipeAdd: () -> Unit,
    onProductAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.secondaryContainer,
                    finalColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                val rotation by remember {
                    derivedStateOf { checkedProgress * 45f }
                }

                val tintColor = lerp(
                    start = MaterialTheme.colorScheme.onSecondaryContainer,
                    stop = MaterialTheme.colorScheme.onSecondary,
                    fraction = checkedProgress
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (expanded) {
                        stringResource(Res.string.action_close)
                    } else {
                        stringResource(Res.string.action_create)
                    },
                    tint = tintColor,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }
        },
        modifier = modifier
    ) {
        FloatingActionButtonMenuItem(
            onClick = onRecipeAdd,
            text = { Text(stringResource(Res.string.headline_recipe)) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_skillet),
                    contentDescription = null
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        FloatingActionButtonMenuItem(
            onClick = onProductAdd,
            icon = {
                Icon(
                    imageVector = Icons.Default.LunchDining,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(Res.string.headline_product)) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(onBack: () -> Unit, onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    var searchState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchState,
            onSearch = onSearch,
            placeholder = { Text(stringResource(Res.string.action_search)) },
            leadingIcon = {
                IconButton(
                    onClick = coroutineScope.lambda {
                        if (searchState.currentValue == SearchBarValue.Expanded) {
                            searchState.animateToCollapsed()
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            },
            trailingIcon = {
                if (textFieldState.text.isEmpty()) {
                    BarcodeScannerIconButton(
                        onClick = coroutineScope.lambda {
                            showBarcodeScanner = true
                            searchState.animateToCollapsed()
                        }
                    )
                } else {
                    IconButton(
                        onClick = { textFieldState.clearText() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }
            }
        )
    }

    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onBarcodeScan = {
                textFieldState.setTextAndPlaceCursorAtEnd(it)
                onSearch(it)
                showBarcodeScanner = false
            },
            onClose = { showBarcodeScanner = false }
        )
    }

    ExpandedFullScreenSearchBar(
        state = searchState,
        inputField = inputField
    ) {
    }

    Scaffold(
        topBar = {
            TopSearchBar(
                state = searchState,
                inputField = inputField
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = paddingValues
        ) {
            itemsIndexed(
                items = List(100) { it }
            ) { i, _ ->
                var isSelected by rememberSaveable { mutableStateOf(false) }
                AddFoodListItem(
                    food = SearchFoodItem(
                        foodId = FoodId.Product(0),
                        name = "Food $i",
                        brand = "Brand $i",
                        calories = NutrientsHelper.calculateCalories(10f, 10f, 10f),
                        proteins = 10f,
                        carbohydrates = 10f,
                        fats = 10f,
                        packageWeight = PortionWeight.Package(100f),
                        servingWeight = null,
                        measurement = Measurement.Package(.5f),
                        measurementId = if (isSelected) {
                            MeasurementId.Product(0)
                        } else {
                            null
                        },
                        uniqueId = i.toString()
                    ),
                    onToggle = {
                        isSelected = !isSelected
                    },
                    modifier = Modifier.clickable { },
                    shape = if (i == 0) {
                        MaterialTheme.shapes.large.copy(
                            bottomEnd = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp)
                        )
                    } else if (i == 99) {
                        MaterialTheme.shapes.large.copy(
                            topEnd = CornerSize(0.dp),
                            topStart = CornerSize(0.dp)
                        )
                    } else {
                        RectangleShape
                    }
                )
            }
        }
    }
}
