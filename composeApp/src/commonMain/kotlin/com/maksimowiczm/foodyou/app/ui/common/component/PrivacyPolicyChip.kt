package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacyPolicyChip(onClick: () -> Unit, modifier: Modifier = Modifier.Companion) {
    AssistChip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = null,
                modifier = Modifier.Companion.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_privacy_policy)) },
    )
}
