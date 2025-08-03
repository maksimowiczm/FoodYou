package com.maksimowiczm.foodyou.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(onDiscard) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = content
    )
}
