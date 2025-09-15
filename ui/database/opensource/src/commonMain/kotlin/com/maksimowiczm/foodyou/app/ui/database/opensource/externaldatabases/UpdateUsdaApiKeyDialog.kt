package com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.search.FoodSearchPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun UpdateUsdaApiKeyDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences> =
        koinInject(named(FoodSearchPreferences::class.qualifiedName!!))
    val foodSearchPreferences =
        foodSearchPreferencesRepository.observe().collectAsStateWithLifecycle(null).value

    if (foodSearchPreferences?.usda == null) {
        return
    }

    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(foodSearchPreferences.usda.apiKey ?: "")

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    runBlocking {
                        val key = textFieldState.text.toString().takeIf { it.isNotBlank() }
                        foodSearchPreferencesRepository.update {
                            copy(usda = usda.copy(apiKey = key))
                        }
                        onSave()
                    }
                }
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
        },
        title = { Text(stringResource(Res.string.action_set_key)) },
        text = {
            OutlinedTextField(
                state = textFieldState,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text(stringResource(Res.string.headline_api_key)) },
            )
        },
    )
}
