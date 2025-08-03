package com.maksimowiczm.foodyou.feature.food.ui.product.download

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
import com.maksimowiczm.foodyou.feature.food.ui.UsdaErrorCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DownloadErrorCard(error: DownloadError, modifier: Modifier = Modifier) {
    when (error) {
        is DownloadError.GenericError -> DownloadGenericErrorCard(error, modifier)
        is DownloadError.Usda -> UsdaErrorCard(error.error, modifier)
    }
}

@Composable
private fun DownloadGenericErrorCard(
    error: DownloadError.GenericError,
    modifier: Modifier = Modifier
) {
    val errorText = when (error) {
        DownloadError.GenericError.URLNotFound ->
            stringResource(Res.string.error_url_not_found)

        DownloadError.GenericError.URLNotSupported ->
            stringResource(Res.string.error_url_is_not_supported)

        DownloadError.GenericError.ProductNotFound ->
            stringResource(Res.string.error_product_not_found)

        is DownloadError.GenericError.Custom if (error.message != null) -> error.message
        is DownloadError.GenericError.Custom -> stringResource(Res.string.error_unknown_error)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.error_failed_to_download_product),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
