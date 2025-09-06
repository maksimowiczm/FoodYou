package com.maksimowiczm.foodyou.feature.food.shared.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.feature.food.shared.ui.DownloadProductUsdaErrorCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchErrorCard(
    error: RemoteFoodException,
    onRetry: () -> Unit,
    onUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (error) {
        is RemoteFoodException.Unknown,
        is RemoteFoodException.OpenFoodFacts.Timeout ->
            FoodSearchErrorCard(message = error.message, onRetry = onRetry, modifier = modifier)

        is RemoteFoodException.ProductNotFoundException -> Unit

        is RemoteFoodException.USDA.ApiKeyDisabledException,
        is RemoteFoodException.USDA.ApiKeyInvalidException,
        is RemoteFoodException.USDA.ApiKeyIsMissingException,
        is RemoteFoodException.USDA.ApiKeyUnauthorizedException,
        is RemoteFoodException.USDA.ApiKeyUnverifiedException,
        is RemoteFoodException.USDA.RateLimitException ->
            DownloadProductUsdaErrorCard(
                error = error,
                onUpdateApiKey = onUsdaApiKey,
                modifier = modifier,
            )
    }
}

@Composable
private fun FoodSearchErrorCard(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDetails by rememberSaveable { mutableStateOf(false) }
    val message = message ?: stringResource(Res.string.error_unknown_error)

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
            Text(
                text = stringResource(Res.string.neutral_an_error_occurred),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.neutral_remote_database_error),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(
                    onClick = { showDetails = !showDetails },
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                ) {
                    Text(stringResource(Res.string.action_show_details))
                }

                FilledTonalButton(
                    onClick = { onRetry() },
                    colors =
                        ButtonDefaults.filledTonalButtonColors(
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(showDetails) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
    }
}
