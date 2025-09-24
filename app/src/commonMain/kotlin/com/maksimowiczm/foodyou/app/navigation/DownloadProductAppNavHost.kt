package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.navigation.DownloadProductAppNavHost.CreateProduct
import com.maksimowiczm.foodyou.app.navigation.DownloadProductAppNavHost.UsdaApiKey
import com.maksimowiczm.foodyou.app.ui.database.externaldatabases.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.app.ui.food.product.CreateProductScreen
import kotlinx.serialization.Serializable

@Composable
fun DownloadProductAppNavHost(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    url: String,
    modifier: Modifier = Modifier.Companion,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CreateProduct(url),
        modifier = modifier,
    ) {
        dialog<UsdaApiKey> {
            UpdateUsdaApiKeyDialog(
                onDismissRequest = { navController.popBackStackInclusive<UsdaApiKey>() },
                onSave = { navController.popBackStackInclusive<UsdaApiKey>() },
            )
        }
        forwardBackwardComposable<CreateProduct> {
            val (url) = it.toRoute<CreateProduct>()

            CreateProductScreen(
                onBack = onBack,
                onCreate = { onCreate() },
                onUpdateUsdaApiKey = { navController.navigateSingleTop(UsdaApiKey) },
                url = url,
            )
        }
    }
}

private object DownloadProductAppNavHost {
    @Serializable object UsdaApiKey

    @Serializable data class CreateProduct(val url: String)
}
