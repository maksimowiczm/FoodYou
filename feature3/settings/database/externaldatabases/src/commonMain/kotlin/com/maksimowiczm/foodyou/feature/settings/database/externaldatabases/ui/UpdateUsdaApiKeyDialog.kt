package com.maksimowiczm.foodyou.feature.settings.database.externaldatabases.ui

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
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUsdaApiKeyCommand
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQuery
import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_cancel
import foodyou.app.generated.resources.action_save
import foodyou.app.generated.resources.action_set_key
import foodyou.app.generated.resources.headline_api_key
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun UpdateUsdaApiKeyDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val queryBus: QueryBus = koinInject()
    val commandBus: CommandBus = koinInject()
    val foodPreferences =
        queryBus
            .dispatch<FoodPreferences>(ObserveFoodPreferencesQuery)
            .collectAsStateWithLifecycle(null)
            .value

    if (foodPreferences?.usda == null) {
        return
    }

    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(foodPreferences.usda.apiKey ?: "")

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
                        commandBus.dispatch<Unit, Unit>(UpdateUsdaApiKeyCommand(key))
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
