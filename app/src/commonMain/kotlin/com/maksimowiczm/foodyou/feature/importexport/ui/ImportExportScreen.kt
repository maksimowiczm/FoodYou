package com.maksimowiczm.foodyou.feature.importexport.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.ExperimentalFeatureCard
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.importexport.domain.csvHeader
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun ImportExportScreen(onBack: () -> Unit, modifier: Modifier = Modifier)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ImportExportScreen(
    onBack: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    events: Flow<ImportExportEvent>,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val clipboardManager = LocalClipboardManager.current
    val header = remember { csvHeader() }

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(Res.string.headline_import_and_export)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            ImportExportSnackbarHost(events)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues
        ) {
            item {
                ExperimentalFeatureCard(
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            item {
                Column {
                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.action_import_food_products))
                        },
                        modifier = Modifier.clickable { onImport() },
                        supportingContent = {
                            Text(stringResource(Res.string.action_import_food_products_from_csv))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.FileOpen,
                                contentDescription = null
                            )
                        }
                    )
                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.action_export_food_products))
                        },
                        modifier = Modifier.clickable { onExport() },
                        supportingContent = {
                            Text(stringResource(Res.string.action_export_food_products_to_csv))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.FilePresent,
                                contentDescription = null
                            )
                        }
                    )
                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.action_copy_header))
                        },
                        modifier = Modifier.clickable { clipboardManager.copy(header, header) },
                        supportingContent = {
                            Text(
                                text = header,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(Res.string.description_import_and_export_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportExportSnackbarHost(
    events: Flow<ImportExportEvent>,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val snackbarHostState = remember { SnackbarHostState() }
    val importStartedString = stringResource(Res.string.neutral_import_started)
    val exportStartedString = stringResource(Res.string.neutral_export_started)
    val unknownError = stringResource(Res.string.error_unknown_error)

    LaunchedEffect(events) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            events.collectLatest {
                val message = when (it) {
                    ImportExportEvent.ExportFailedToStart -> unknownError
                    ImportExportEvent.ExportStarted -> exportStartedString
                    ImportExportEvent.ImportFailedToStart -> unknownError
                    ImportExportEvent.ImportStarted -> importStartedString
                }

                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    )
}
