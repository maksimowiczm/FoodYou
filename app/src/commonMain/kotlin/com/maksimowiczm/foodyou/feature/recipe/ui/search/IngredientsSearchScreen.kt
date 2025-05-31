package com.maksimowiczm.foodyou.feature.recipe.ui.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.core.ui.ext.plus
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.recipe.domain.IngredientSearchItem
import com.maksimowiczm.foodyou.feature.recipe.ui.IngredientListItem
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_clear
import foodyou.app.generated.resources.action_search
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun IngredientsSearchScreen(
    onBack: () -> Unit,
    onIngredient: (IngredientSearchItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IngredientsSearchViewModel = koinViewModel()
) {
    val ingredients = viewModel.ingredients.collectAsStateWithLifecycle().value

    IngredientsSearchScreen(
        ingredients = ingredients,
        onBack = onBack,
        onSearch = viewModel::onSearch,
        onIngredient = onIngredient,
        modifier = modifier
    )
}

@OptIn(
    FlowPreview::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
private fun IngredientsSearchScreen(
    ingredients: List<IngredientSearchItem>?,
    onBack: () -> Unit,
    onSearch: (String?) -> Unit,
    onIngredient: (IngredientSearchItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onClose = { showBarcodeScanner = false },
            onBarcodeScan = {
                showBarcodeScanner = false
                onSearch(it)
            }
        )
    }

    val textFieldState = rememberTextFieldState()

    val latestOnSearch by rememberUpdatedState(onSearch)
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text }
            .debounce(1_000)
            .collectLatest { latestOnSearch(it.toString()) }
    }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            state = textFieldState,
            onSearch = onSearch,
            expanded = false,
            onExpandedChange = {},
            placeholder = { Text(stringResource(Res.string.action_search)) },
            leadingIcon = { ArrowBackIconButton(onBack) },
            trailingIcon = {
                if (textFieldState.text.isEmpty()) {
                    BarcodeScannerIconButton({ showBarcodeScanner = true })
                } else {
                    IconButton(
                        onClick = {
                            textFieldState.clearText()
                            onSearch(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                contentAlignment = Alignment.TopCenter
            ) {
                inputField()
            }
        }
    ) { paddingValues ->
        if (ingredients != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = paddingValues + PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(
                    items = ingredients,
                    key = { _, item -> item.uniqueId }
                ) { i, ingredient ->
                    val topStart = animateDpAsState(
                        targetValue = if (i == 0) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val topEnd = animateDpAsState(
                        targetValue = if (i == 0) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val bottomStart = animateDpAsState(
                        targetValue = if (i == ingredients.size - 1) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    val bottomEnd = animateDpAsState(
                        targetValue = if (i == ingredients.size - 1) 16.dp else 0.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                    ).value.coerceAtLeast(0.dp)

                    var shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                    IngredientListItem(
                        ingredient = ingredient,
                        onClick = { onIngredient(ingredient) },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = shape
                    )
                }
            }
        }
    }
}
