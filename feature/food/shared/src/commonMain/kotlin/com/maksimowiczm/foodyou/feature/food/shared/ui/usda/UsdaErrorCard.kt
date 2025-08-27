package com.maksimowiczm.foodyou.feature.food.shared.ui.usda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.food.application.DownloadProductError
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun UsdaErrorCard(
    error: DownloadProductError.Usda,
    onUpdateApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val obtainKeyUrl = stringResource(Res.string.link_usda_obtain_key)

    val errorText =
        when (error) {
            DownloadProductError.Usda.ApiKeyInvalid ->
                stringResource(Res.string.error_api_key_is_invalid)

            DownloadProductError.Usda.ApiKeyUnverified ->
                stringResource(Res.string.error_usda_not_verified)

            DownloadProductError.Usda.RateLimit -> stringResource(Res.string.error_usda_rate_limit)
        }

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
                    text = stringResource(Res.string.headline_food_data_central_usda),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Text(text = errorText, style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    onClick = onUpdateApiKey,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_set_key))
                }

                Button(
                    onClick = { uriHandler.openUri(obtainKeyUrl) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_obtain_key))
                }
            }
        }
    }
}
