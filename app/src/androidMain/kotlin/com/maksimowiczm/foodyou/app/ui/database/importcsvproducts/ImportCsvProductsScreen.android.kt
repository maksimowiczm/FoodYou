package com.maksimowiczm.foodyou.app.ui.database.importcsvproducts

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.SomethingWentWrongScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun ImportCsvProductsScreen(onBack: () -> Unit, onFinish: () -> Unit, modifier: Modifier) {
    val viewModel: ImportCsvProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                onBack()
            } else {
                viewModel.handleCsv(uri, context)
            }
        }

    LaunchedEffect(uiState) {
        when (uiState) {
            UiState.WaitingForFile -> launcher.launch(arrayOf("text/csv", "*/*"))
            else -> Unit
        }
    }

    when (uiState) {
        is UiState.WithError ->
            SomethingWentWrongScreen(
                onBack = onBack,
                message = uiState.message,
                modifier = modifier,
            )

        is UiState.FileOpened ->
            ImportCsvProductsScreen(
                header = uiState.header.sorted(),
                onBack = onBack,
                onImport = viewModel::import,
                modifier = modifier,
            )

        UiState.WaitingForFile ->
            Scaffold(modifier) { paddingValues ->
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
            }

        is UiState.ImportSuccess -> ImportingSuccessScreen(uiState.count, onBack, modifier)
        is UiState.Importing -> ImportingProductsScreen(uiState.count, modifier)
    }
}
