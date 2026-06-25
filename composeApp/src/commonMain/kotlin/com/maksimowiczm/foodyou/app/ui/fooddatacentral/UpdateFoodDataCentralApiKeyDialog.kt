package com.maksimowiczm.foodyou.app.ui.fooddatacentral

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalAppConfig
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun UpdateFoodDataCentralApiKeyDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val repository: FoodDataCentralSettingsRepository = koinInject()
    val appConfig = LocalAppConfig.current
    val uriHandler = LocalUriHandler.current

    val settings =
        remember(repository) { repository.observe() }.collectAsStateWithLifecycle(null).value
            ?: return

    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(settings.apiKey ?: "")
    var hidePassword by rememberSaveable { mutableStateOf(true) }

    val onCommit = {
        runBlocking {
            val key = textFieldState.text.toString().takeIf { it.isNotBlank() }
            repository.update { it.copy(apiKey = key) }
            onSave()
        }
    }

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FilledTonalButton(onClick = onCommit, enabled = textFieldState.text.isNotBlank()) {
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
                TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
            }
        },
        title = { Row { Text(stringResource(Res.string.action_set_key)) } },
        text = {
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
                        if (hidePassword) {
                            Icon(Icons.Outlined.Visibility, stringResource(Res.string.action_show))
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
                onKeyboardAction = { onCommit() },
            )
        },
    )
}
