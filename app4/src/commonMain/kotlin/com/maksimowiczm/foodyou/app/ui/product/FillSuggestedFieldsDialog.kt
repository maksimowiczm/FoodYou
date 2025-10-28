package com.maksimowiczm.foodyou.app.ui.product

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FillSuggestedFieldsDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onSkip) { Text(stringResource(Res.string.action_skip_for_now)) }
        },
        dismissButton = {
            TextButton(onConfirm) { Text(stringResource(Res.string.action_fill_in_details)) }
        },
        title = { Text(stringResource(Res.string.headline_add_more_details)) },
        text = { Text(stringResource(Res.string.description_add_more_details_product_form)) },
    )
}
