package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.navigation.DownloadProductAppNavHost.CreateProduct
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
        forwardBackwardComposable<CreateProduct> {
            val (url) = it.toRoute<CreateProduct>()

            CreateProductScreen(
                onBack = onBack,
                onCreate = { onCreate() },
                url = url,
            )
        }
    }
}

private object DownloadProductAppNavHost {
    @Serializable data class CreateProduct(val url: String)
}
