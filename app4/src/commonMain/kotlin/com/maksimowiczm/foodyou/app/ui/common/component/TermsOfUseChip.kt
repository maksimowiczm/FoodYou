package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_terms_of_use
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsOfUseChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_terms_of_use)) },
    )
}
