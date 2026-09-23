package com.maksimowiczm.foodyou.capabilities.foodbrowsing.openfoodfacts

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.features.openfoodfacts.OpenFoodFactsLoginDialog
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.shared.ui.utility.LocalAppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenFoodFactsErrorCard(error: OpenFoodFactsApiError, modifier: Modifier = Modifier) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    val errorText =
        when (error) {
            is OpenFoodFactsApiError.ProductNotFound -> return
            is OpenFoodFactsApiError.RateLimitExceeded ->
                stringResource(Res.string.error_open_food_facts_timeout)

            is OpenFoodFactsApiError.ServiceUnavailable ->
                stringResource(Res.string.error_open_food_facts_service_unavailable)

            is OpenFoodFactsApiError.Unknown -> error.message ?: return
        }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        OpenFoodFactsLoginDialog(
            onDismissRequest = { showDialog = false },
            onSave = { showDialog = false },
        )
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
                    text = stringResource(Res.string.headline_open_food_facts),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(text = errorText, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    onClick = { uriHandler.openUri(appConfig.openFoodFactsRegisterUri) },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(text = stringResource(Res.string.action_register))
                }
                Button(
                    onClick = { showDialog = true },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(text = stringResource(Res.string.action_sign_in))
                }
            }
        }
    }
}
