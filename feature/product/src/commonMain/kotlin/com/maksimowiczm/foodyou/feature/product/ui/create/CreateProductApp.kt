package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.rememberProductFormState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null
) = key(text) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (text != null) {
            Download(text)
        } else {
            Create(null)
        },
        modifier = modifier
    ) {
        forwardBackwardComposable<Create> {
            val (json) = it.toRoute<Create>()

            val product = if (json != null) {
                Json.decodeFromString<RemoteProduct>(json)
            } else {
                null
            }

            val state = when (product) {
                null -> rememberProductFormState()
                else -> rememberProductFormState(product)
            }

            CreateProductApp(
                onBack = onBack,
                onCreate = onCreate,
                onDownload = {
                    navController.navigate(Download(null)) { launchSingleTop = true }
                },
                productFormState = state
            )
        }
        forwardBackwardComposable<Download> {
            val (text) = it.toRoute<Download>()

            DownloadProductScreen(
                text = text,
                onBack = {
                    if (text != null) {
                        onBack()
                    }

                    navController.popBackStack<Download>(inclusive = true)
                },
                onDownload = {
                    val json = Json.encodeToString(it)
                    navController.navigate(Create(json)) {
                        launchSingleTop = true
                        popUpTo<Create> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Serializable
private data class Download(val link: String?)

@Serializable
private data class Create(val productJson: String?)
