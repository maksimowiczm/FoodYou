package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
