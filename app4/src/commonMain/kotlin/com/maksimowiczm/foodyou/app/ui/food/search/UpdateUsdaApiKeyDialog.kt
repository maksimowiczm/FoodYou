package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettingsRepository
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun UpdateUsdaApiKeyDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val coroutineScope = rememberCoroutineScope()
    val repository: FoodDataCentralSettingsRepository = koinInject()
    val uriHandler = LocalUriHandler.current
    val obtainKeyUrl = "https://fdc.nal.usda.gov/api-key-signup"

    val settings =
        repository.observe().collectAsStateWithLifecycle(runBlocking { repository.load() }).value

    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(settings.apiKey ?: "")

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val key = textFieldState.text.toString().takeIf { it.isNotBlank() }
                        repository.save(settings.copy(apiKey = key))
                        onSave()
                    }
                }
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        modifier = modifier,
        dismissButton = {
            Row {
                TextButton(onClick = { uriHandler.openUri(obtainKeyUrl) }) {
                    Text(stringResource(Res.string.action_obtain_key))
                }
                TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
            }
        },
        title = { Row { Text(stringResource(Res.string.action_set_key)) } },
        text = {
            OutlinedTextField(
                state = textFieldState,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text(stringResource(Res.string.headline_api_key)) },
            )
        },
    )
}
