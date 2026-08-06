package com.maksimowiczm.foodyou.features.food.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UserFoodMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.headline_delete_food)) },
            text = { Text(stringResource(Res.string.description_delete_food)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(text = stringResource(Res.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = stringResource(Res.string.action_cancel))
                }
            },
        )
    }

    Box(modifier) {
        IconButton(
            onClick = { expanded = true },
            enabled = enabled,
            shapes = IconButtonDefaults.shapes(),
            colors = colors,
        ) {
            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
        }
        DropdownMenuPopup(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, 2)) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.action_edit)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                    },
                    onClick = {
                        expanded = false
                        onEdit()
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.action_delete)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                    },
                    onClick = {
                        expanded = false
                        showDeleteDialog = true
                    },
                )
            }
        }
    }
}
