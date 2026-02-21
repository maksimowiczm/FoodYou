package com.maksimowiczm.foodyou.app.ui.database.externaldatabases

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences
import foodyou.app.generated.resources.*
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

    val textFieldState = rememberTextFieldState(foodSearchPreferences.usda.apiKey ?: "")

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
                placeholder = { Text(stringResource(Res.string.headline_api_key)) },
            )
        },
    )
}
