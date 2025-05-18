package com.maksimowiczm.foodyou.feature.importexport.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun ImportExportScreen(onBack: () -> Unit, modifier: Modifier) {
    AndroidImportExportScreen(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun AndroidImportExportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImportExportViewModel = koinViewModel()
) {
    val dateFormatter = LocalDateFormatter.current
    val appName = stringResource(Res.string.app_name)

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onImport(it) }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { viewModel.onExport(it) }
    }

    val onImport = { importLauncher.launch(arrayOf("text/csv", "*/*")) }
    val onExport = {
        val time = viewModel.getCurrentDateTime()
        val formattedDate = dateFormatter.formatDateSuperShort(time.date)
        val formattedTime = dateFormatter.formatTime(time.time)
        val fileName = buildString {
            append(appName)
            append(" ")
            append(formattedDate)
            append(" ")
            append(formattedTime)
        }.replace(" ", "-").replace(":", "-").replace(".", "-")

        exportLauncher.launch("$fileName.csv")
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Android33PermissionProxy(
            onBack = onBack,
            onImport = onImport,
            onExport = onExport,
            modifier = modifier
        )
    } else {
        ImportExportScreenImpl(
            onBack = onBack,
            onImport = onImport,
            onExport = onExport,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun Android33PermissionProxy(
    onBack: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current

    var requestInSettings by remember { mutableStateOf(false) }
    val importWithPermissionLauncher = rememberLauncherForNotificationPermission(
        onNotGranted = { requestInSettings = true },
        onGranted = { onImport() }
    )

    val exportWithPermissionLauncher = rememberLauncherForNotificationPermission(
        onNotGranted = { requestInSettings = true },
        onGranted = { onExport() }
    )

    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    if (requestInSettings && !permissionState.status.isGranted) {
        AndroidNotificationsRedirectToSettingsAlertDialog(
            onDismissRequest = { requestInSettings = false },
            onConfirm = {
                activity?.let { redirectToNotificationsSettings(it) }
                requestInSettings = false
            }
        )
    }

    ImportExportScreenImpl(
        onBack = onBack,
        onImport = {
            if (permissionState.status.isGranted) {
                onImport()
            } else {
                importWithPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onExport = {
            if (permissionState.status.isGranted) {
                onExport()
            } else {
                exportWithPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        modifier = modifier
    )
}

@Suppress("ktlint:compose:parameter-naming")
@Composable
private fun rememberLauncherForNotificationPermission(
    onNotGranted: () -> Unit,
    onGranted: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val activity = LocalActivity.current

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (
            activity != null &&
            !isGranted &&
            !shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
        ) {
            onNotGranted()
        }

        if (isGranted) {
            onGranted()
        }
    }
}

private fun redirectToNotificationsSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }

    context.startActivity(intent)
}

@Composable
private fun AndroidNotificationsRedirectToSettingsAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_go_to_settings))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = {
            Text(stringResource(Res.string.headline_permission_required))
        },
        text = {
            Text(
                stringResource(Res.string.description_notifications_permission_required_data_sync)
            )
        }
    )
}
