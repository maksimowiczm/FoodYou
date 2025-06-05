package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import foodyou.app.generated.resources.*
import java.util.Collections
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal actual fun DownloadProductScreen(
    text: String?,
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier
) {
    val viewModel = koinViewModel<DownloadProductScreenViewModel>(
        parameters = { parametersOf(text) }
    )

    val isMutating = viewModel.isMutating.collectAsStateWithLifecycle().value
    val error = viewModel.error.collectAsStateWithLifecycle().value
    val textFieldState = rememberTextFieldState(text ?: "")

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.productEvent.collect(onDownload)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val suggestDatabaseUrl = stringResource(Res.string.link_github_issue)

    val uriHandler = LocalUriHandler.current
    val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)
    val context = LocalContext.current
    val onOpenFoodFacts = remember(context, uriHandler, openFoodFactsUrl) {
        {
            val packageName = CustomTabsClient.getPackageName(context, Collections.emptyList())

            if (packageName == null) {
                uriHandler.openUri(openFoodFactsUrl)
            } else {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(context, openFoodFactsUrl.toUri())
            }
        }
    }

    val usdaUrl = stringResource(Res.string.link_usda)
    val onUsda = remember(context, uriHandler, usdaUrl) {
        {
            val packageName = CustomTabsClient.getPackageName(context, Collections.emptyList())

            if (packageName == null) {
                uriHandler.openUri(usdaUrl)
            } else {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(context, usdaUrl.toUri())
            }
        }
    }
    val usdaObtainKeyUrl = stringResource(Res.string.link_usda_obtain_key)
    val onUsdaObtainKey = remember(context, uriHandler, usdaObtainKeyUrl) {
        {
            val packageName = CustomTabsClient.getPackageName(context, Collections.emptyList())

            if (packageName == null) {
                uriHandler.openUri(usdaObtainKeyUrl)
            } else {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(context, usdaObtainKeyUrl.toUri())
            }
        }
    }

    DownloadProductScreen(
        isMutating = isMutating,
        error = error,
        textFieldState = textFieldState,
        onBack = onBack,
        onDownload = { viewModel.onDownload(textFieldState.text.toString()) },
        onPaste = {
            clipboardManager
                .paste()
                ?.takeIf { it.isNotEmpty() }
                ?.let { textFieldState.setTextAndPlaceCursorAtEnd(it) }
        },
        onOpenFoodFacts = onOpenFoodFacts,
        onUsda = onUsda,
        onUsdaSetApiKey = viewModel::setUsdaApiKey,
        onUsdaObtainApiKey = onUsdaObtainKey,
        onSuggestDatabase = { uriHandler.openUri(suggestDatabaseUrl) },
        modifier = modifier
    )
}
