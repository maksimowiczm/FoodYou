package com.maksimowiczm.foodyou.app.ui.database.opensource.exportcsvproducts

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.OpenSourceAppConfig
import com.maksimowiczm.foodyou.app.ui.shared.component.SomethingWentWrongScreen
import java.time.LocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun ExportCsvProductsScreen(onBack: () -> Unit, onFinish: () -> Unit, modifier: Modifier) {
    val viewModel: ExportProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/csv")
        ) { uri ->
            if (uri == null) {
                onBack()
            } else {
                viewModel.handleCsv(uri, context)
            }
        }

    val appConfig: OpenSourceAppConfig = koinInject()
    val fileName = remember {
        "Food You ${appConfig.versionName}-products-${LocalDateTime.now()}.csv"
    }
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Error,
            is UiState.Exported,
            is UiState.Exporting -> Unit

            UiState.WaitingForFile -> launcher.launch(fileName)
        }
    }

    when (uiState) {
        is UiState.Error ->
            SomethingWentWrongScreen(
                onBack = onBack,
                message = uiState.message,
                modifier = modifier,
            )

        is UiState.Exported ->
            SuccessScreen(count = uiState.count, onBack = onBack, modifier = modifier)

        UiState.WaitingForFile -> Unit

        is UiState.Exporting -> ExportingProductsScreen(count = uiState.count, modifier = modifier)
    }
}
