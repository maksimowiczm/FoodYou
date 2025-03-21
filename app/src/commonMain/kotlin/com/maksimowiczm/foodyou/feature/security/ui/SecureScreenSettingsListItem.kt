package com.maksimowiczm.foodyou.feature.security.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.security.data.SecurityPreferences
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun SecureScreenSettingsListItem(
    modifier: Modifier = Modifier,
    // It is just one boolean flag, lets not bother with a ViewModel for this
    dataStore: DataStore<Preferences> = koinInject()
) {
    val checked by dataStore
        .observe(SecurityPreferences.hideContent)
        .filterNotNull()
        .collectAsStateWithLifecycle(false)

    SecureScreenSettingsListItem(
        checked = checked,
        onCheckedChange = { checked ->
            runBlocking {
                dataStore.set(SecurityPreferences.hideContent to checked)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun SecureScreenSettingsListItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_secure_screen))
        },
        modifier = modifier.clickable { onCheckedChange(!checked) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(stringResource(Res.string.action_prevent_screen_capture))
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Preview
@Composable
private fun SecureScreenSettingsListItemPreview() {
    FoodYouTheme {
        SecureScreenSettingsListItem(checked = true, onCheckedChange = {})
    }
}
