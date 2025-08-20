package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.navigation.domain.CreateProductDestination
import com.maksimowiczm.foodyou.navigation.domain.UsdaApiKeyDestination
import com.maksimowiczm.foodyou.navigation.graph.food.foodNavigationGraphBuilder
import com.maksimowiczm.foodyou.navigation.graph.settings.settingsDatabaseNavigationGraph

@Composable
fun DownloadProductNavHost(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    url: String,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CreateProductDestination(url = url),
        modifier = modifier,
    ) {
        foodNavigationGraphBuilder(
            updateProductOnBack = {},
            updateProductOnSave = {},
            updateRecipeOnBack = {},
            updateRecipeOnSave = {},
            updateRecipeOnEditFood = {},
            onUpdateUsdaApiKey = {},
            createProductOnBack = onBack,
            createProductOnCreate = { onCreate() },
            createOnUpdateUsdaApiKey = { navController.navigateSingleTop(UsdaApiKeyDestination) },
        )
        settingsDatabaseNavigationGraph(
            masterOnBack = {},
            masterOnExternalDatabases = {},
            masterOnImportCsvProducts = {},
            masterOnExportCsvProducts = {},
            masterOnDatabaseDump = {},
            externalDatabasesOnBack = {},
            externalDatabasesOnSwissFoodCompositionDatabase = {},
            usdaApiKeyOnDismiss = { navController.popBackStack<UsdaApiKeyDestination>(true) },
            usdaApiKeyOnSave = { navController.popBackStack<UsdaApiKeyDestination>(true) },
            databaseDumpOnBack = {},
            databaseDumpOnSuccess = {},
            swissFoodCompositionDatabaseOnBack = {},
            importCsvProductsOnBack = {},
            importCsvProductsOnFinish = {},
            exportCsvProductsOnBack = {},
            exportCsvProductsOnFinish = {},
        )
    }
}
