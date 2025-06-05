package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAException
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DownloadErrorCard(error: DownloadError, modifier: Modifier = Modifier) {
    val errorText = when (error) {
        DownloadError.URLNotFound -> stringResource(Res.string.error_url_not_found)
        DownloadError.URLNotSupported -> stringResource(Res.string.error_url_is_not_supported)
        DownloadError.ProductNotFound -> stringResource(Res.string.error_product_not_found)
        is DownloadError.Custom if (error.message != null) -> error.message
        is DownloadError.Custom -> stringResource(Res.string.error_unknown_error)
        is DownloadError.UsdaApiKeyError -> return
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null
            )
            Text(
                text = stringResource(Res.string.error_failed_to_download_product),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
internal fun UsdaApiKeyErrorCard(
    error: DownloadError.UsdaApiKeyError,
    onSetKey: () -> Unit,
    onObtainKey: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (error.error) {
        is USDAException.ApiKeyIsMissingException,
        is USDAException.ApiKeyDisabledException,
        is USDAException.ApiKeyInvalidException,
        is USDAException.ApiKeyUnauthorizedException,
        is USDAException.ApiKeyUnverifiedException -> stringResource(
            Res.string.headline_food_data_central_usda
        )

        is USDAException.RateLimitException -> stringResource(Res.string.error_usda_rate_limit)
    }

    val message = when (error.error) {
        is USDAException.ApiKeyIsMissingException,
        is USDAException.ApiKeyDisabledException,
        is USDAException.ApiKeyInvalidException,
        is USDAException.ApiKeyUnauthorizedException ->
            error.error.message ?: stringResource(Res.string.error_api_key_is_invalid)

        is USDAException.ApiKeyUnverifiedException -> stringResource(
            Res.string.error_usda_not_verified
        )

        is USDAException.RateLimitException -> stringResource(
            Res.string.error_usda_rate_limit_description
        )
    }

    val colors = when (error.error) {
        is USDAException.ApiKeyIsMissingException,
        is USDAException.ApiKeyDisabledException,
        is USDAException.ApiKeyInvalidException,
        is USDAException.ApiKeyUnauthorizedException,
        is USDAException.ApiKeyUnverifiedException -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )

        is USDAException.RateLimitException -> CardDefaults.cardColors()
    }

    Card(
        modifier = modifier,
        colors = colors
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(
                    onClick = onSetKey
                ) {
                    Text(stringResource(Res.string.action_set_key))
                }
                Button(
                    onClick = onObtainKey
                ) {
                    Text(stringResource(Res.string.action_obtain_key))
                }
            }
        }
    }
}
