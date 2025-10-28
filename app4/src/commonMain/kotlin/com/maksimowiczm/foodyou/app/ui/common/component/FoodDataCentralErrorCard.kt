package com.maksimowiczm.foodyou.app.ui.common.component

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
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.food.search.UpdateUsdaApiKeyDialog
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodDataCentralErrorCard(
    error: FoodDatabaseError.FoodDataCentral,
    modifier: Modifier = Modifier.Companion,
) {
    val errorText =
        when (error) {
            is FoodDatabaseError.FoodDataCentral.ApiKeyDisabled,
            is FoodDatabaseError.FoodDataCentral.ApiKeyInvalid,
            is FoodDatabaseError.FoodDataCentral.ApiKeyIsMissing,
            is FoodDatabaseError.FoodDataCentral.ApiKeyUnauthorized ->
                stringResource(Res.string.error_api_key_is_invalid)

            is FoodDatabaseError.FoodDataCentral.ApiKeyUnverified ->
                stringResource(Res.string.error_usda_not_verified)

            is FoodDatabaseError.FoodDataCentral.RateLimitExceeded ->
                stringResource(Res.string.error_usda_rate_limit)
        }

    var showApiKeyDialog by rememberSaveable { mutableStateOf(false) }
    if (showApiKeyDialog) {
        UpdateUsdaApiKeyDialog(
            onDismissRequest = { showApiKeyDialog = false },
            onSave = { showApiKeyDialog = false },
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
            modifier = Modifier.Companion.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Companion.CenterVertically,
            ) {
                Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null)
                Text(
                    text = stringResource(Res.string.headline_food_data_central_usda),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Text(text = errorText, style = MaterialTheme.typography.bodyMedium)

            Button(
                onClick = { showApiKeyDialog = true },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer,
                    ),
            ) {
                Text(stringResource(Res.string.action_set_key))
            }
        }
    }
}
