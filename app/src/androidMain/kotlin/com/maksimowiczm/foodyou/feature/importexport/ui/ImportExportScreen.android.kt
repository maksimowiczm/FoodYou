package com.maksimowiczm.foodyou.feature.importexport.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun ImportExportScreen(onBack: () -> Unit, modifier: Modifier) {
    AndroidImportExportScreen(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun AndroidImportExportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImportExportViewModel = koinViewModel()
) {
    val dateFormatter = LocalDateFormatter.current
    val appName = stringResource(Res.string.app_name)

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onImport(it) }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { viewModel.onExport(it) }
    }

    ImportExportScreenImpl(
        onBack = onBack,
        onImport = { importLauncher.launch(arrayOf("text/csv", "*/*")) },
        onExport = {
            val time = viewModel.getCurrentDateTime()
            val formattedDate = dateFormatter.formatDateSuperShort(time.date)
            val formattedTime = dateFormatter.formatTime(time.time)
            val fileName = buildString {
                append(appName)
                append(" ")
                append(formattedDate)
                append(" ")
                append(formattedTime)
            }.replace(" ", "-").replace(":", "-").replace(".", "-")

            exportLauncher.launch("$fileName.csv")
        },
        modifier = modifier
    )
}
