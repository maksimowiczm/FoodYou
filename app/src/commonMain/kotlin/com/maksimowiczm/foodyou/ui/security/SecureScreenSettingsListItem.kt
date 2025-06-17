package com.maksimowiczm.foodyou.ui.security

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import com.maksimowiczm.foodyou.preferences.HideContent
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.filterNotNull
import org.jetbrains.compose.resources.stringResource

@Composable
fun SecureScreenSettingsListItem(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    // It is just one boolean flag, lets not bother with a ViewModel for this
    hideContent: HideContent = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()
    val checked by hideContent
        .observe()
        .filterNotNull()
        .collectAsStateWithLifecycle(false)

    SecureScreenSettingsListItem(
        checked = checked,
        onCheckedChange = coroutineScope.lambda<Boolean> {
            hideContent.set(it)
        },
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
private fun SecureScreenSettingsListItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_secure_screen))
        },
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.action_prevent_screen_capture))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
