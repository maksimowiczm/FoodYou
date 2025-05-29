package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_search
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.jetbrains.compose.resources.stringResource

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
internal fun IngredientsSearchScreen(
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onClose = { showBarcodeScanner = false },
            onBarcodeScan = {
                // TODO
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
            trailingIcon = { BarcodeScannerIconButton({ showBarcodeScanner = true }) }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            items(100) { index ->
                Text(text = "Search Result #$index", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
