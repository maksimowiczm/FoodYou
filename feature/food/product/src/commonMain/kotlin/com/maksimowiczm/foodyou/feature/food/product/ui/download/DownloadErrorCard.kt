package com.maksimowiczm.foodyou.feature.food.product.ui.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.core.food.domain.usecase.DownloadProductError
import com.maksimowiczm.foodyou.feature.food.shared.ui.DownloadProductUsdaErrorCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DownloadErrorCard(
    error: DownloadProductError,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (error) {
        DownloadProductError.UrlNotFound ->
            DownloadErrorCard(
                message = stringResource(Res.string.error_url_not_found),
                modifier = modifier,
            )

        DownloadProductError.UrlNotSupported ->
            DownloadErrorCard(
                message = stringResource(Res.string.error_url_is_not_supported),
                modifier = modifier,
            )

        is DownloadProductError.RemoteFoodError ->
            DownloadErrorCard(
                error = error.exception,
                onUpdateUsdaApiKey = onUpdateUsdaApiKey,
                modifier = modifier,
            )
    }
}

@Composable
private fun DownloadErrorCard(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null)
                Text(
                    text = stringResource(Res.string.error_failed_to_download_product),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DownloadErrorCard(
    error: RemoteFoodException,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text =
        when (error) {
            is RemoteFoodException.OpenFoodFacts.Timeout -> error.message

            is RemoteFoodException.ProductNotFoundException ->
                stringResource(Res.string.error_product_not_found)

            is RemoteFoodException.USDA ->
                return DownloadProductUsdaErrorCard(
                    error = error,
                    onUpdateApiKey = onUpdateUsdaApiKey,
                    modifier = modifier,
                )

            is RemoteFoodException.Unknown -> error.message
        } ?: stringResource(Res.string.error_unknown_error)

    DownloadErrorCard(message = text, modifier = modifier)
}
