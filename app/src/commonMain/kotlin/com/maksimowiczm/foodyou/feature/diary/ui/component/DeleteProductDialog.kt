package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_cancel
import foodyou.app.generated.resources.action_delete
import foodyou.app.generated.resources.description_delete_product
import foodyou.app.generated.resources.headline_delete_product
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteProductDialog(onDismissRequest: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.headline_delete_product)
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.description_delete_product)
            )
        }
    )
}
