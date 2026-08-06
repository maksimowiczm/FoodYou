package com.maksimowiczm.foodyou.shared.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResetToDefaultDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_reset))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = { Text(stringResource(Res.string.headline_reset_to_default)) },
        text = text,
    )
}
