package com.maksimowiczm.foodyou.feature.about.master.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.about.master.presentation.PreviewReleaseDialogViewModel
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewReleaseDialog(modifier: Modifier = Modifier) {
    val viewModel: PreviewReleaseDialogViewModel = koinViewModel()
    val showDialog by viewModel.showDialog.collectAsStateWithLifecycle()

    if (showDialog)
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Row {
                    TextButton(onClick = viewModel::dontShowAgain) { Text(stringResource(Res.string.action_dont_show_again)) }
                    TextButton(onClick = viewModel::dismissDialog) {
                        Text(stringResource(Res.string.positive_ok))
                    }
                }
            },
            modifier = modifier,
            icon = { Icon(imageVector = Icons.Outlined.Warning, contentDescription = null) },
            title = { Text(stringResource(Res.string.headline_preview_release)) },
            text = { Text(stringResource(Res.string.description_preview_release)) },
        )
}
