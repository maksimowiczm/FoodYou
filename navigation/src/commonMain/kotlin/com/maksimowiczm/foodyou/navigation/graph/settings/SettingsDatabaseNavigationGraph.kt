package com.maksimowiczm.foodyou.navigation.graph.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.feature.settings.database.databasedump.ui.DatabaseDumpScreen
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui.ExternalDatabasesScreen
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.feature.settings.database.master.ui.DatabaseSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.database.swissfoodcompositiondatabase.ui.SwissFoodCompositionDatabaseScreen
import com.maksimowiczm.foodyou.navigation.domain.DumpDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsExternalDatabasesDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsSwissFoodCompositionDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.UsdaApiKeyDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.settingsDatabaseNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnExternalDatabases: () -> Unit,
    masterOnDatabaseDump: () -> Unit,
    externalDatabasesOnBack: () -> Unit,
    externalDatabasesOnSwissFoodCompositionDatabase: () -> Unit,
    usdaApiKeyOnDismiss: () -> Unit,
    usdaApiKeyOnSave: () -> Unit,
    databaseDumpOnBack: () -> Unit,
    databaseDumpOnSuccess: () -> Unit,
    swissFoodCompositionDatabaseOnBack: () -> Unit,
) {
    navigation<SettingsDatabaseDestination>(startDestination = SettingsDatabaseMasterDestination) {
        forwardBackwardComposable<SettingsDatabaseMasterDestination> {
            DatabaseSettingsScreen(
                onBack = masterOnBack,
                onExternalDatabases = masterOnExternalDatabases,
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
    }
}
