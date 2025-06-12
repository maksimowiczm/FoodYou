package com.maksimowiczm.foodyou.ui.externaldatabases

import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.net.toUri
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.link_github_issue
import foodyou.app.generated.resources.link_open_food_facts
import foodyou.app.generated.resources.link_usda
import java.util.Collections
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ExternalDatabasesScreen(
    onBack: () -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit,
    modifier: Modifier
) {
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

    val githubIssueUrl = stringResource(Res.string.link_github_issue)
    val onSuggestDatabase = { uriHandler.openUri(githubIssueUrl) }

    ExternalDatabasesScreen(
        onBack = onBack,
        onOpenFoodFacts = onOpenFoodFacts,
        onFoodDataCentral = onUsda,
        onSwissFoodCompositionDatabase = onSwissFoodCompositionDatabase,
        onSuggestDatabase = onSuggestDatabase,
        modifier = modifier
    )
}
