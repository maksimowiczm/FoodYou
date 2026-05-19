package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClipboardManager

@Composable
internal fun FoodSource(
    url: String,
    logo: @Composable () -> Unit,
    headline: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { uriHandler.openUri(url) },
                    onLongClick = { clipboardManager.copy("url", url) },
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        logo()
        Column {
            Text(
                text = headline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
    }
}

internal object FoodSourceDefaults {
    val logoMaxSize = 32.dp
}
