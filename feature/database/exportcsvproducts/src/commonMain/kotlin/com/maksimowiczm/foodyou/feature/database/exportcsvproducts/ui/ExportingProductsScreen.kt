package com.maksimowiczm.foodyou.feature.database.exportcsvproducts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ExportingProductsScreen(count: Int, modifier: Modifier = Modifier) {
    val coroutinesScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pleaseWaitMessage = stringResource(Res.string.headline_please_wait)

    BackHandler(
        onBack = { coroutinesScope.launch { snackbarHostState.showSnackbar(pleaseWaitMessage) } }
    )

    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularWavyProgressIndicator(modifier = Modifier.size(68.dp))

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.notification_exporting_products),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.description_please_wait_while_exporting_products),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(8.dp))

            Text(text = count.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
}
