package com.maksimowiczm.foodyou.feature.about.master.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.about.master.presentation.Changelog
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewReleaseDialog(modifier: Modifier = Modifier) {
    val changelog: Changelog = koinInject()
    val currentVersion = remember { changelog.currentVersion }

    // Show every time
    var showDialog by rememberSaveable { mutableStateOf(true) }

    if (showDialog && currentVersion?.isPreview == true) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.positive_ok))
                }
            },
            modifier = modifier,
            icon = { Icon(imageVector = Icons.Outlined.Warning, contentDescription = null) },
            title = { Text(stringResource(Res.string.headline_preview_release)) },
            text = { Text(stringResource(Res.string.description_preview_release)) },
        )
    }
}
