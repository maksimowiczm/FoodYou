package com.maksimowiczm.foodyou.app.ui.food.diary.shared.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.food.shared.component.Icon
import com.maksimowiczm.foodyou.app.ui.food.shared.component.stringResource
import com.maksimowiczm.foodyou.shared.compose.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_source
import org.jetbrains.compose.resources.stringResource

@Composable
fun Source(source: FoodSource, modifier: Modifier = Modifier) {
    val url = source.url
    val clipboardManger = LocalClipboardManager.current
    val sourceStr = stringResource(Res.string.headline_source)

    Row(
        modifier =
            modifier.clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    if (url != null) {
                        clipboardManger.copy(label = sourceStr, text = url)
                    }
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        source.type.Icon()

        Column {
            Text(text = source.type.stringResource(), style = MaterialTheme.typography.bodyMedium)
            if (url != null) {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
