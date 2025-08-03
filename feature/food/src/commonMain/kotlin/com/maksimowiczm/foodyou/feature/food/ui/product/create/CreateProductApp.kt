package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.food.ui.product.download.DownloadProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.product.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.rememberProductFormState
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.link_github_issue
import foodyou.app.generated.resources.link_open_food_facts
import foodyou.app.generated.resources.link_usda
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier,
    url: String? = null
) = key(url) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (url != null) {
            Download(url)
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

            CreateProductScreen(
                state = state,
                onBack = onBack,
                onCreate = onCreate,
                onDownload = {
                    navController.navigate(Download(null)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        forwardBackwardComposable<Download> {
            val (url) = it.toRoute<Download>()

            val viewModel = koinViewModel<DownloadProductViewModel> {
                parametersOf(url)
            }

            LaunchedCollectWithLifecycle(viewModel.productEvent) {
                val json = Json.encodeToString(it)
                navController.navigate(Create(json)) {
                    launchSingleTop = true

                    if (url != null) {
                        popUpTo<Download> {
                            inclusive = true
                        }
                    } else {
                        popUpTo<Create> {
                            inclusive = true
                        }
                    }
                }
            }

            val textFieldState = rememberTextFieldState(url ?: "")
            val clipboardManager = LocalClipboardManager.current

            val isDownloading by viewModel.isMutating.collectAsStateWithLifecycle()
            val error by viewModel.error.collectAsStateWithLifecycle()

            val uriHandler = LocalUriHandler.current
            val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)
            val usdaUrl = stringResource(Res.string.link_usda)
            val suggestDatabaseUrl = stringResource(Res.string.link_github_issue)

            DownloadProductScreen(
                isDownloading = isDownloading,
                error = error,
                textFieldState = textFieldState,
                onBack = { navController.popBackStack<Download>(true) },
                onDownload = { viewModel.onDownload(textFieldState.text.toString()) },
                onPaste = {
                    val text = clipboardManager.paste()
                    if (text != null) {
                        textFieldState.setTextAndPlaceCursorAtEnd(text)
                    }
                },
                onOpenFoodFacts = { uriHandler.openUri(openFoodFactsUrl) },
                onUsda = { uriHandler.openUri(usdaUrl) },
                onSuggestDatabase = { uriHandler.openUri(suggestDatabaseUrl) }
            )
        }
    }
}

@Serializable
private data class Create(val productJson: String?)

@Serializable
private data class Download(val url: String?)
