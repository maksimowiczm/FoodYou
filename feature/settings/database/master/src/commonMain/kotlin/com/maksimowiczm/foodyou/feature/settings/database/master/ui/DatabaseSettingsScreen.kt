package com.maksimowiczm.foodyou.feature.settings.database.master.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DatabaseSettingsScreen(
    onBack: () -> Unit,
    onExternalDatabases: () -> Unit,
    onImportCsvProducts: () -> Unit,
    onDatabaseDump: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_database)) },
                subtitle = { Text(stringResource(Res.string.description_manage_database)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item { ExternalDatabasesSettingsListItem(onExternalDatabases) }
            item { ImportCsvProductsSettingsListItem(onImportCsvProducts) }
            item { DatabaseDumpSettingsListItem(onDatabaseDump) }
        }
    }
}

@Composable
private fun ExternalDatabasesSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SettingsListItem(
        icon = { Icon(imageVector = Icons.Outlined.CloudDownload, contentDescription = null) },
        label = { Text(stringResource(Res.string.headline_external_databases)) },
        supportingContent = { Text(stringResource(Res.string.description_external_databases)) },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun DatabaseDumpSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SettingsListItem(
        icon = { Icon(painterResource(Res.drawable.ic_file_export), null) },
        label = { Text(stringResource(Res.string.headline_database_dump)) },
        supportingContent = { Text(stringResource(Res.string.description_database_dump)) },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun ImportCsvProductsSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SettingsListItem(
        icon = { Icon(Icons.Outlined.FileOpen, null) },
        label = { Text(stringResource(Res.string.action_import_csv_food_products)) },
        supportingContent = {
            Text(stringResource(Res.string.description_import_csv_food_products))
        },
        onClick = onClick,
        modifier = modifier,
    )
}
