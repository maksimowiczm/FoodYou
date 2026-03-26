package com.maksimowiczm.foodyou.app.ui.database.externaldatabases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.food.domain.repository.OpenFoodFactsCredentialsRepository
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun OpenFoodFactsLoginDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val repository: OpenFoodFactsCredentialsRepository = koinInject()

    val login = rememberTextFieldState()
    val password = rememberTextFieldState()

    val isLoginValid by remember(login) { derivedStateOf { login.text.isNotBlank() } }
    val isPasswordValid by remember(password) { derivedStateOf { password.text.isNotBlank() } }
    val isFormValid by
        remember(login, password) {
            derivedStateOf { login.text.isNotBlank() && password.text.isNotBlank() }
        }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = isFormValid,
                onClick = {
                    if (!isFormValid) return@TextButton

                    scope.launch {
                        repository.store(login.text.toString(), password.text.toString())
                        onSave()
                    }
                },
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
        },
        title = { Text(stringResource(Res.string.action_sign_in)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text =
                        stringResource(Res.string.description_open_food_facts_credentials_message),
                    style = MaterialTheme.typography.bodySmall,
                )
                OutlinedTextField(
                    state = login,
                    placeholder = { Text(stringResource(Res.string.headline_username)) },
                    isError = !isLoginValid,
                    supportingText = {
                        if (!isLoginValid) {
                            Text(stringResource(Res.string.neutral_required))
                        }
                    },
                )
                SecureTextField(
                    state = password,
                    placeholder = { Text(stringResource(Res.string.headline_password)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
