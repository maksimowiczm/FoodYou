package com.maksimowiczm.foodyou.app.ui.database.tbca

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.common.compose.extension.add
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description2_tbca
import foodyou.app.generated.resources.headline_tbca
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun TBCAScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinInject<TBCAViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TBCAScreen(
        uiState = uiState,
        onBack = onBack,
        onImport = viewModel::import,
        onReset = viewModel::reset,
        modifier = modifier,
    )
}

@Composable
private fun TBCAScreen(
    uiState: TBCAUiState,
    onBack: () -> Unit,
    onImport: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_tbca)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = onBack,
                        enabled = uiState !is TBCAUiState.Importing,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (uiState) {
                is TBCAUiState.Initial -> {
                    InitialState(
                        onImport = onImport,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }

                is TBCAUiState.Importing -> {
                    ImportingProgress(
                        progress = uiState.progress,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(paddingValues),
                    )
                }

                is TBCAUiState.Finished -> {
                    ImportingFinished(
                        onReset = onReset,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(paddingValues),
                    )
                }

                is TBCAUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        onReset = onReset,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialState(
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
    ) {
        item {
            Text(
                text = stringResource(Res.string.description2_tbca),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        item {
            Button(
                onClick = onImport,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Import Brazilian Foods")
            }
        }
    }
}

@Composable
private fun ImportingProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(64.dp),
        )

        Text(
            text = "Importing Brazilian foods...",
            style = MaterialTheme.typography.titleMedium,
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.8f),
        )

        val percentage = (progress * 100).toInt()
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ImportingFinished(
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onReset()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "Import Complete!",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Brazilian foods are now available in your search.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Import Failed",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onReset) {
            Text("Try Again")
        }
    }
}
