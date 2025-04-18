package com.maksimowiczm.foodyou.feature.productredesign.ui.create

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.input.Rule
import com.maksimowiczm.foodyou.core.input.dsl.checks
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_this_field_is_required
import org.jetbrains.compose.resources.stringResource

internal data class DownloadProductFailed(val throwable: Throwable)

internal sealed interface OpenFoodFactsLinkError {
    object Empty : OpenFoodFactsLinkError
    object InvalidUrl : OpenFoodFactsLinkError
}

internal object OpenFoodFactsLinkRules {
    val NotEmpty = Rule<OpenFoodFactsLinkError> {
        { it.isNotBlank() } checks { OpenFoodFactsLinkError.Empty }
    }

    val ValidUrl = Rule<OpenFoodFactsLinkError> {
        {
            val regex = "https://.+\\.openfoodfacts\\.org/product/(\\d+)".toRegex()
            val matchResult = regex.find(it)
            matchResult != null && matchResult.groupValues.size > 1
        } checks { OpenFoodFactsLinkError.InvalidUrl }
    }
}

@Composable
internal fun Iterable<OpenFoodFactsLinkError>.stringResource(): String {
    @Suppress("SimplifiableCallChain") // Can't call @Composable from lambda
    return map { it.stringResource() }.joinToString("\n")
}

@Composable
private fun OpenFoodFactsLinkError.stringResource(): String = when (this) {
    is OpenFoodFactsLinkError.Empty -> stringResource(Res.string.error_this_field_is_required)
    OpenFoodFactsLinkError.InvalidUrl -> "Invalid URL"
}
