package com.maksimowiczm.foodyou.navigation.graph.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.app.ui.database.opensource.databasedump.DatabaseDumpScreen
import com.maksimowiczm.foodyou.app.ui.database.opensource.exportcsvproducts.ExportCsvProductsScreen
import com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases.ExternalDatabasesScreen
import com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.app.ui.database.opensource.importcsvproducts.ImportCsvProductsScreen
import com.maksimowiczm.foodyou.app.ui.database.opensource.master.DatabaseSettingsScreen
import com.maksimowiczm.foodyou.app.ui.database.opensource.swissfoodcompositiondatabase.SwissFoodCompositionDatabaseScreen
import com.maksimowiczm.foodyou.navigation.domain.DumpDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.ExportCsvProductsDestination
import com.maksimowiczm.foodyou.navigation.domain.ImportCsvProductsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsExternalDatabasesDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsSwissFoodCompositionDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.UsdaApiKeyDestination
import com.maksimowiczm.foodyou.shared.compose.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.settingsDatabaseNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnExternalDatabases: () -> Unit,
    masterOnImportCsvProducts: () -> Unit,
    masterOnExportCsvProducts: () -> Unit,
    masterOnDatabaseDump: () -> Unit,
    externalDatabasesOnBack: () -> Unit,
    externalDatabasesOnSwissFoodCompositionDatabase: () -> Unit,
    usdaApiKeyOnDismiss: () -> Unit,
    usdaApiKeyOnSave: () -> Unit,
    databaseDumpOnBack: () -> Unit,
    databaseDumpOnSuccess: () -> Unit,
    swissFoodCompositionDatabaseOnBack: () -> Unit,
    importCsvProductsOnBack: () -> Unit,
    importCsvProductsOnFinish: () -> Unit,
    exportCsvProductsOnBack: () -> Unit,
    exportCsvProductsOnFinish: () -> Unit,
) {
    navigation<SettingsDatabaseDestination>(startDestination = SettingsDatabaseMasterDestination) {
        forwardBackwardComposable<SettingsDatabaseMasterDestination> {
            DatabaseSettingsScreen(
                onBack = masterOnBack,
                onExternalDatabases = masterOnExternalDatabases,
                onImportCsvProducts = masterOnImportCsvProducts,
                onExportCsvProducts = masterOnExportCsvProducts,
                onDatabaseDump = masterOnDatabaseDump,
            )
        }
        forwardBackwardComposable<SettingsExternalDatabasesDestination> {
            ExternalDatabasesScreen(
                onBack = externalDatabasesOnBack,
                onSwissFoodCompositionDatabase = externalDatabasesOnSwissFoodCompositionDatabase,
            )
        }
        dialog<UsdaApiKeyDestination> {
            UpdateUsdaApiKeyDialog(
                onDismissRequest = usdaApiKeyOnDismiss,
                onSave = usdaApiKeyOnSave,
            )
        }
        forwardBackwardComposable<DumpDatabaseDestination> {
            DatabaseDumpScreen(onBack = databaseDumpOnBack, onSuccess = databaseDumpOnSuccess)
        }
        forwardBackwardComposable<SettingsSwissFoodCompositionDatabaseDestination> {
            SwissFoodCompositionDatabaseScreen(onBack = swissFoodCompositionDatabaseOnBack)
        }
        forwardBackwardComposable<ImportCsvProductsDestination> {
            ImportCsvProductsScreen(
                onBack = importCsvProductsOnBack,
                onFinish = importCsvProductsOnFinish,
            )
        }
        forwardBackwardComposable<ExportCsvProductsDestination> {
            ExportCsvProductsScreen(
                onBack = exportCsvProductsOnBack,
                onFinish = exportCsvProductsOnFinish,
            )
        }
    }
}
