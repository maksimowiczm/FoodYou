package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetToDefaultDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onConfirm) {
                Text(stringResource(Res.string.action_reset))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = { Text(stringResource(Res.string.headline_reset_to_default)) },
        text = text
    )
}
