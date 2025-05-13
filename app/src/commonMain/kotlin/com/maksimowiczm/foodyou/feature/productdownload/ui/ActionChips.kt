package com.maksimowiczm.foodyou.feature.productdownload.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_browse_open_food_facts
import foodyou.app.generated.resources.action_paste_url
import foodyou.app.generated.resources.link_open_food_facts
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ActionChips(
    isMutating: Boolean,
    onPaste: (String) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val uriHandler = LocalUriHandler.current
    val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)

    ActionChips(
        isMutating = isMutating,
        onPaste = onPaste,
        onOpenFoodFacts = { uriHandler.openUri(openFoodFactsUrl) },
        modifier = modifier
    )
}

@Composable
private fun ActionChips(
    isMutating: Boolean,
    onPaste: (String) -> Unit,
    onOpenFoodFacts: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val clipboardManager = LocalClipboardManager.current

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = {
                val text = clipboardManager.paste()
                if (text != null && text.isNotEmpty()) {
                    onPaste(text)
                }
            },
            label = {
                Text(stringResource(Res.string.action_paste_url))
            },
            enabled = !isMutating,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(AssistChipDefaults.IconSize)
                )
            }
        )
        AssistChip(
            onClick = onOpenFoodFacts,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(AssistChipDefaults.IconSize)
                )
            },
            label = {
                Text(stringResource(Res.string.action_browse_open_food_facts))
            }
        )
    }
}
