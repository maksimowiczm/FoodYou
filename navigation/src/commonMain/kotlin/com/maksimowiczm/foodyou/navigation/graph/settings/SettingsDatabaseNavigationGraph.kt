package com.maksimowiczm.foodyou.navigation.graph.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui.ExternalDatabasesScreen
import com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.feature.settings.database.master.ui.DatabaseSettingsScreen
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsExternalDatabasesDestination
import com.maksimowiczm.foodyou.navigation.domain.UsdaApiKeyDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.settingsDatabaseNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnExternalDatabases: () -> Unit,
    externalDatabasesOnBack: () -> Unit,
    externalDatabasesOnSwissFoodCompositionDatabase: () -> Unit,
    usdaApiKeyOnDismiss: () -> Unit,
    usdaApiKeyOnSave: () -> Unit,
) {
    navigation<SettingsDatabaseDestination>(startDestination = SettingsDatabaseMasterDestination) {
        forwardBackwardComposable<SettingsDatabaseMasterDestination> {
            DatabaseSettingsScreen(
                onBack = masterOnBack,
                onExternalDatabases = masterOnExternalDatabases,
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
    }
}
