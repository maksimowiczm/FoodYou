package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.preferences.UsdaApiKey
import com.maksimowiczm.foodyou.feature.usda.USDAException
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_obtain_key
import foodyou.app.generated.resources.action_save
import foodyou.app.generated.resources.action_set_key
import foodyou.app.generated.resources.error_api_key_is_invalid
import foodyou.app.generated.resources.error_usda_not_verified
import foodyou.app.generated.resources.error_usda_rate_limit
import foodyou.app.generated.resources.headline_api_key
import foodyou.app.generated.resources.headline_food_data_central_usda
import foodyou.app.generated.resources.link_usda_obtain_key
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UsdaErrorCard(error: USDAException, modifier: Modifier = Modifier.Companion) {
    val usdaApiKeyPreference = userPreference<UsdaApiKey>()

    val uriHandler = LocalUriHandler.current
    val obtainKeyUrl = stringResource(Res.string.link_usda_obtain_key)

    val errorText = when (error) {
        is USDAException.ProductNotFoundException -> stringResource(
            Res.string.error_api_key_is_invalid
        )

        is USDAException.ApiKeyIsMissingException -> stringResource(
            Res.string.error_api_key_is_invalid
        )

        is USDAException.RateLimitException -> stringResource(Res.string.error_usda_rate_limit)
        is USDAException.ApiKeyInvalidException -> stringResource(
            Res.string.error_api_key_is_invalid
        )

        is USDAException.ApiKeyDisabledException -> stringResource(
            Res.string.error_api_key_is_invalid
        )

        is USDAException.ApiKeyUnauthorizedException -> stringResource(
            Res.string.error_api_key_is_invalid
        )

        is USDAException.ApiKeyUnverifiedException -> stringResource(
            Res.string.error_usda_not_verified
        )
    }

    var showUsdaApiKeyPrompt by rememberSaveable { mutableStateOf(false) }

    if (showUsdaApiKeyPrompt) {
        val focusRequester = remember { FocusRequester() }
        val textFieldState = rememberTextFieldState(usdaApiKeyPreference.getBlocking() ?: "")

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
        }

        AlertDialog(
            onDismissRequest = { showUsdaApiKeyPrompt = false },
            title = { Text(stringResource(Res.string.action_set_key)) },
            text = {
                OutlinedTextField(
                    state = textFieldState,
                    modifier = Modifier.Companion.focusRequester(focusRequester),
                    placeholder = { Text(stringResource(Res.string.headline_api_key)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUsdaApiKeyPrompt = false
                        val key = textFieldState.text.toString().takeIf { it.isNotBlank() }
                        usdaApiKeyPreference.setBlocking(key)
                    }
                ) {
                    Text(stringResource(Res.string.action_save))
                }
            }
        )
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier.Companion.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.headline_food_data_central_usda),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Companion.End)
            ) {
                Button(
                    onClick = { showUsdaApiKeyPrompt = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_set_key))
                }

                Button(
                    onClick = { uriHandler.openUri(obtainKeyUrl) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(stringResource(Res.string.action_obtain_key))
                }
            }
        }
    }
}
