package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.maksimowiczm.foodyou.core.ui.component.BackHandler

@Composable
internal fun CreateRecipeScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    CreateRecipeScreen(
        onClose = onClose,
        onCreate = {},
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRecipeScreen(
    onClose: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }

    // TODO
    val handleClose = {
        onClose()
    }

    BackHandler(
        // TODO
        enabled = true
    ) {
        showDiscardDialog = true
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topBar = @Composable {
        TopAppBar(
            title = { Text(stringResource(Res.string.headline_create_recipe)) },
            navigationIcon = {
                IconButton(
                    onClick = handleClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.action_close)
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = onCreate,
                    // TODO
                    enabled = false
                ) {
                    Text(stringResource(Res.string.action_create))
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {

        }
    }
}

@Composable
private fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(Res.string.question_discard_recipe))
        }
    )
}