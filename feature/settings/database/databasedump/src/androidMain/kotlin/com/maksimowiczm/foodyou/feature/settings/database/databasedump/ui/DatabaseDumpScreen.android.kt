package com.maksimowiczm.foodyou.feature.settings.database.databasedump.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.shared.application.config.AppConfig
import com.maksimowiczm.foodyou.business.shared.application.database.DatabaseDumpService
import com.maksimowiczm.foodyou.feature.settings.database.databasedump.presentation.DatabaseDumpViewModel
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.ext.now
import foodyou.app.generated.resources.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun DatabaseDumpScreen(onBack: () -> Unit, onSuccess: () -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    val viewModel: DatabaseDumpViewModel = koinViewModel()
    val appConfig: AppConfig = koinInject()
    val databaseDumpProvider: DatabaseDumpService = koinInject()

    var uiState by rememberSaveable { mutableStateOf(DatabaseDumpScreenUiState.WaitingForFileName) }

    val failureMessage = stringResource(Res.string.error_failed_to_generate_database_dump)
    val onFailure = {
        Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
        onBack()
    }

    val successMessage = stringResource(Res.string.neutral_database_dump_generated_successfully)
    val onSuccess = {
        Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
        onSuccess()
    }

    val fileName = remember { "Food You ${appConfig.versionName}-${LocalDateTime.now()}.db" }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/vnd.sqlite3")
        ) { uri ->
            if (uri == null) {
                onBack()
            } else {
                val stream = context.contentResolver.openOutputStream(uri)

                if (stream == null) {
                    onFailure()
                    return@rememberLauncherForActivityResult
                }

                uiState = DatabaseDumpScreenUiState.GeneratingDump

                val minimumTime = viewModel.viewModelScope.async { delay(2_000) }
                val dumpJob =
                    viewModel.viewModelScope.async {
                        stream.use {
                            databaseDumpProvider.provideDatabaseDump().collect(stream::write)
                        }
                    }

                viewModel.viewModelScope.launch {
                    awaitAll(minimumTime, dumpJob)
                    uiState = DatabaseDumpScreenUiState.DumpGenerated
                }
            }
        }

    val latestOnSuccess by rememberUpdatedState(onSuccess)
    LaunchedEffect(uiState) {
        when (uiState) {
            DatabaseDumpScreenUiState.WaitingForFileName -> launcher.launch(fileName)
            DatabaseDumpScreenUiState.GeneratingDump -> Unit
            DatabaseDumpScreenUiState.DumpGenerated -> latestOnSuccess()
        }
    }

    when (uiState) {
        DatabaseDumpScreenUiState.GeneratingDump -> {
            BackHandler {}

            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    stroke =
                        Stroke(
                            width = with(LocalDensity.current) { 6.dp.toPx() },
                            cap = StrokeCap.Round,
                        ),
                )
                Text(stringResource(Res.string.neutral_generating_database_dump))
            }
        }

        DatabaseDumpScreenUiState.WaitingForFileName,
        DatabaseDumpScreenUiState.DumpGenerated -> Unit
    }
}
