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
fun DiscardChangesDialog(
    onDismissRequest: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { Text(stringResource(Res.string.question_discard_changes)) },
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(onDiscard, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = content,
    )
}
