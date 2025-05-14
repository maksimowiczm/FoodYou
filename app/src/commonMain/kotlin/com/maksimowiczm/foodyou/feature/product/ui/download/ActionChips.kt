package com.maksimowiczm.foodyou.feature.product.ui.download

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
import androidx.compose.ui.platform.testTag
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
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)

    val clipboardManager = LocalClipboardManager.current

    ActionChips(
        isMutating = isMutating,
        onPaste = {
            clipboardManager
                .paste()
                ?.takeIf { it.isNotEmpty() }
                ?.let { onPaste(it) }
        },
        onOpenFoodFacts = { uriHandler.openUri(openFoodFactsUrl) },
        modifier = modifier
    )
}

@Composable
private fun ActionChips(
    isMutating: Boolean,
    onPaste: () -> Unit,
    onOpenFoodFacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onPaste,
            modifier = Modifier.testTag(DownloadProductScreenTestTags.PASTE_URL_CHIP),
            label = { Text(stringResource(Res.string.action_paste_url)) },
            enabled = !isMutating,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        AssistChip(
            onClick = onOpenFoodFacts,
            modifier = Modifier.testTag(DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            },
            label = {
                Text(stringResource(Res.string.action_browse_open_food_facts))
            }
        )
    }
}
