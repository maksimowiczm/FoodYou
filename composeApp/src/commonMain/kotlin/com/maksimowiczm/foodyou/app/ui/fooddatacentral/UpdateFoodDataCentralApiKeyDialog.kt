package com.maksimowiczm.foodyou.app.ui.fooddatacentral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiKeyVerificationService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun UpdateFoodDataCentralApiKeyDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    service: FoodDataCentralApiKeyVerificationService = koinInject(),
) {
    val repository: FoodDataCentralSettingsRepository = koinInject()
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    val settings =
        remember(repository) { repository.observe() }.collectAsStateWithLifecycle(null).value
            ?: return

    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(settings.apiKey ?: "")
    var hidePassword by rememberSaveable { mutableStateOf(true) }

    var requestInProgress by rememberSaveable { mutableStateOf(false) }
    var verificationError by rememberSaveable { mutableStateOf<Throwable?>(null) }

    val onCommit = {
        val key = textFieldState.text.toString().trim()
        if (key.isNotBlank()) {
            requestInProgress = true
            verificationError = null

            scope.launch {
                runCatching { service.verify(key) }
                    .onFailure { verificationError = it }
                    .onSuccess {
                        repository.update { it.copy(apiKey = key) }
                        onSave()
                    }
                requestInProgress = false
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FilledTonalButton(
                onClick = onCommit,
                shapes = ButtonDefaults.shapes(),
                enabled = textFieldState.text.isNotBlank() && !requestInProgress,
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        modifier = modifier,
        dismissButton = {
            Row {
                TextButton(
                    onClick = { uriHandler.openUri(appConfig.foodDataCentralObtainApiKeyUri) }
                ) {
                    Text(stringResource(Res.string.action_obtain_key))
                }
                TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        },
        title = { Text(stringResource(Res.string.action_set_key)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (requestInProgress) LinearProgressIndicator(Modifier.fillMaxWidth())
                else Spacer(Modifier.height(4.dp))

                verificationError?.let { error ->
                    val message =
                        when (error) {
                            is FoodDataCentralApiError.ApiKeyInvalid,
                            is FoodDataCentralApiError.ApiKeyIsMissing,
                            is FoodDataCentralApiError.ApiKeyDisabled,
                            is FoodDataCentralApiError.ApiKeyUnauthorized ->
                                stringResource(Res.string.error_api_key_is_invalid)

                            is FoodDataCentralApiError.RateLimitExceeded ->
                                stringResource(Res.string.error_usda_rate_limit)

                            is FoodDataCentralApiError.ApiKeyUnverified ->
                                stringResource(Res.string.error_usda_not_verified)

                            else -> error.message ?: stringResource(Res.string.error_unknown_error)
                        }

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                OutlinedSecureTextField(
                    state = textFieldState,
                    modifier =
                        Modifier.focusRequester(focusRequester).semantics {
                            contentType = ContentType.Password
                        },
                    label = { Text(stringResource(Res.string.headline_api_key)) },
                    trailingIcon = {
                        IconButton(
                            onClick = { hidePassword = !hidePassword },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            if (hidePassword)
                                Icon(
                                    Icons.Outlined.Visibility,
                                    stringResource(Res.string.action_show),
                                )
                            else
                                Icon(
                                    Icons.Outlined.VisibilityOff,
                                    stringResource(Res.string.action_hide),
                                )
                        }
                    },
                    textObfuscationMode =
                        if (hidePassword) TextObfuscationMode.RevealLastTyped
                        else TextObfuscationMode.Visible,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                    onKeyboardAction = { onCommit() },
                    isError = verificationError != null,
                )
            }
        },
    )
}
