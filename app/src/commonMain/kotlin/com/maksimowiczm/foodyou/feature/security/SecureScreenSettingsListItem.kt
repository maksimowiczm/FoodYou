package com.maksimowiczm.foodyou.feature.security

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.feature.security.ui.SecureScreenSettingsListItem

@Composable
fun SecureScreenSettingsListItem(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SecureScreenSettingsListItem(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    )
}
