package com.maksimowiczm.foodyou.app.ui.food.product.create

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.app.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.app.ui.food.product.ProductFormState
import com.maksimowiczm.foodyou.app.ui.food.product.download.DownloadProductHolder
import com.maksimowiczm.foodyou.app.ui.food.product.download.DownloadProductScreen
import com.maksimowiczm.foodyou.app.ui.food.product.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.app.ui.food.product.rememberProductFormState
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.common.compose.utility.LocalClipboardManager
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier,
    url: String? = null,
) =
    key(url) {
        val navController = rememberNavController()
        val holder = koinViewModel<DownloadProductHolder>()

        NavHost(
            navController = navController,
            startDestination = if (url != null) Download(url) else Create,
            modifier = modifier,
        ) {
            forwardBackwardComposable<Create> {
                val product = holder.product.collectAsStateWithLifecycle().value

                val state =
                    when (product) {
                        null -> rememberProductFormState()
                        else -> rememberProductFormState(product)
                    }

                CreateProductScreen(
                    state = state,
                    onBack = onBack,
                    onCreate = onCreate,
                    onDownload = {
                        navController.navigate(Download(null)) { launchSingleTop = true }
                    },
                )
            }
            forwardBackwardComposable<Download> {
                val (url) = it.toRoute<Download>()

                val viewModel =
                    koinViewModel<DownloadProductViewModel> { parametersOf(url, holder) }

                LaunchedCollectWithLifecycle(viewModel.productEvent) {
                    navController.navigate(Create) {
                        launchSingleTop = true

                        if (url != null) {
                            popUpTo<Download> { inclusive = true }
                        } else {
                            popUpTo<Create> { inclusive = true }
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
                )
            }
        }
    }

@Serializable private data object Create

@Serializable private data class Download(val url: String?)
