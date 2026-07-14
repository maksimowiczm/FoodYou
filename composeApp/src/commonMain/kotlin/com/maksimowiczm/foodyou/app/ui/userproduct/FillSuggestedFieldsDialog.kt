package com.maksimowiczm.foodyou.app.ui.userproduct

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FillSuggestedFieldsDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onSkip, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_skip_for_now))
            }
        },
        dismissButton = {
            TextButton(onConfirm, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_fill_in_details))
            }
        },
        title = { Text(stringResource(Res.string.headline_add_more_details)) },
        text = { Text(stringResource(Res.string.description_add_more_details_product_form)) },
    )
}
