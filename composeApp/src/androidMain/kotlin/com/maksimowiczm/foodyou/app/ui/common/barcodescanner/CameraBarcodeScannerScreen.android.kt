package com.maksimowiczm.foodyou.app.ui.common.barcodescanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier,
) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val activity = LocalActivity.current

    var requestInSettings by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted ->
            if (
                activity != null &&
                    !isGranted &&
                    !shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            ) {
                requestInSettings = true
            }
        }

    val context = LocalContext.current
    if (requestInSettings && !permissionState.status.isGranted) {
        RedirectToSettingsAlertDialog(
            onDismissRequest = { requestInSettings = false },
            onConfirm = {
                redirectToSettings(context)
                requestInSettings = false
            },
        )
    }

    Box(modifier = modifier) {
        Box(modifier = Modifier.safeGesturesPadding().align(Alignment.TopEnd).zIndex(1f)) {
            FilledIconButton(onClick = onClose, shapes = IconButtonDefaults.shapes()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.action_close),
                )
            }
        }

        if (permissionState.status.isGranted) {
            val hapticFeedback = LocalHapticFeedback.current
            CameraBarcodeScanner(
                onBarcodeScan = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onBarcodeScan(it)
                }
            )
        } else {
            RequestCameraPermissionScreen(
                onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                shouldShowRationale = permissionState.status.shouldShowRationale,
            )
        }
    }
}

private fun redirectToSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    context.startActivity(intent)
}
