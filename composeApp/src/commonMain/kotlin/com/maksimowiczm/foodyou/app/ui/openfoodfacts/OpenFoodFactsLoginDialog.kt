package com.maksimowiczm.foodyou.app.ui.openfoodfacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
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
import androidx.compose.material3.OutlinedTextField
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
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.infrastructure.crypto.encryptString
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsLoginService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun OpenFoodFactsLoginDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    service: OpenFoodFactsLoginService = koinInject(),
    repository: OpenFoodFactsSettingsRepository = koinInject(),
) {
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }

    val login = rememberTextFieldState()
    val password = rememberTextFieldState()

    val isLoginValid by remember(login) { derivedStateOf { login.text.isNotBlank() } }
    val isPasswordValid by remember(password) { derivedStateOf { password.text.isNotBlank() } }
    val isFormValid by
        remember(login, password) {
            derivedStateOf { login.text.isNotBlank() && password.text.isNotBlank() }
        }

    var requestInProgress by rememberSaveable { mutableStateOf(false) }
    var authenticationFailure by rememberSaveable { mutableStateOf(false) }
    var hidePassword by rememberSaveable { mutableStateOf(true) }

    val onSignIn = {
        if (isFormValid) {
            requestInProgress = true

            scope.launch {
                val username = login.text.toString()
                val password = password.text.toString()

                runCatching { service.login(username, password) }
                    .onFailure { authenticationFailure = true }
                    .onSuccess {
                        runBlocking {
                            repository.update {
                                it.copy(
                                    credentials =
                                        OpenFoodFactsCredentials(
                                            login = SoftwareEncrypted.encryptString(username),
                                            password = SoftwareEncrypted.encryptString(password),
                                        )
                                )
                            }
                            onSave()
                        }
                    }

                requestInProgress = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FilledTonalButton(
                onClick = onSignIn,
                shapes = ButtonDefaults.shapes(),
                enabled = isFormValid && !requestInProgress,
            ) {
                Text(stringResource(Res.string.action_sign_in))
            }
        },
        modifier = modifier,
        dismissButton = {
            Row {
                TextButton(onClick = { uriHandler.openUri(appConfig.openFoodFactsRegisterUri) }) {
                    Text(stringResource(Res.string.action_register))
                }
                TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        },
        title = { Text(stringResource(Res.string.action_sign_in)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (requestInProgress) LinearProgressIndicator(Modifier.fillMaxWidth())
                else Spacer(Modifier.height(4.dp))
                Text(
                    text =
                        stringResource(Res.string.description_open_food_facts_credentials_message),
                    style = MaterialTheme.typography.bodySmall,
                )
                if (authenticationFailure) {
                    Text(
                        text =
                            stringResource(Res.string.error_open_food_facts_failed_to_authenticate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                OutlinedTextField(
                    state = login,
                    modifier =
                        Modifier.focusRequester(focusRequester).semantics {
                            contentType = ContentType.Username
                        },
                    label = { Text(stringResource(Res.string.headline_username)) },
                    isError = !isLoginValid,
                    supportingText = {
                        if (!isLoginValid) {
                            Text(stringResource(Res.string.neutral_required))
                        }
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                OutlinedSecureTextField(
                    state = password,
                    modifier = Modifier.semantics { contentType = ContentType.Password },
                    label = { Text(stringResource(Res.string.headline_password)) },
                    trailingIcon = {
                        IconButton(
                            onClick = { hidePassword = !hidePassword },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            if (hidePassword) {
                                Icon(
                                    Icons.Outlined.Visibility,
                                    stringResource(Res.string.action_show),
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.VisibilityOff,
                                    stringResource(Res.string.action_hide),
                                )
                            }
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
                    onKeyboardAction = { onSignIn() },
                    isError = !isPasswordValid,
                    supportingText = {
                        if (!isPasswordValid) {
                            Text(stringResource(Res.string.neutral_required))
                        }
                    },
                )
            }
        },
    )
}
