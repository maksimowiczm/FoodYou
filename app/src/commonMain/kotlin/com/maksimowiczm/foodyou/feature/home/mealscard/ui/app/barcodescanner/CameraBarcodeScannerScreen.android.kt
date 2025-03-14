package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.maksimowiczm.foodyou.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    onClose: () -> Unit,
    barcodeScannerScreen: BarcodeScannerScreen,
    modifier: Modifier = Modifier
) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val activity = LocalActivity.current

    var requestInSettings by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (
            activity != null &&
            !isGranted &&
            !shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        ) {
            requestInSettings = true
        }
    }

    val context = LocalContext.current
    if (requestInSettings && !permissionState.status.isGranted) {
        RedirectToSettingsAlertDialog(
            onDismissRequest = { requestInSettings = false },
            onConfirm = { redirectToSettings(context) }
        )
    }

    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .safeGesturesPadding()
                .align(Alignment.TopEnd)
                .zIndex(1f)
        ) {
            FilledIconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.action_close)
                )
            }
        }

        if (permissionState.status.isGranted) {
            barcodeScannerScreen(
                onBarcodeScan = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onBarcodeScan(it)
                },
                modifier = Modifier
            )
        } else {
            RequestCameraPermissionScreen(
                onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                shouldShowRationale = permissionState.status.shouldShowRationale
            )
        }
    }
}

@Composable
private fun RequestCameraPermissionScreen(
    onRequest: () -> Unit,
    shouldShowRationale: Boolean,
    modifier: Modifier = Modifier
) {
    val text = if (shouldShowRationale) {
        stringResource(Res.string.neutral_barcode_scanner_camera_request_rationale)
    } else {
        stringResource(Res.string.neutral_barcode_scanner_camera_request)
    }

    Surface(
        modifier = modifier,
        onClick = onRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.action_tap_to_allow_access),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun RedirectToSettingsAlertDialog(
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
                stringResource(
                    Res.string.neutral_barcode_scanner_camera_request_redirect_to_settings
                )
            )
        }
    )
}

private fun redirectToSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Preview
@Composable
private fun RequestCameraPermissionScreenPreview(
    @PreviewParameter(BooleanPreviewParameter::class) shouldShowRationale: Boolean
) {
    FoodYouTheme {
        RequestCameraPermissionScreen(
            onRequest = {},
            shouldShowRationale = shouldShowRationale
        )
    }
}

@Preview
@Composable
private fun RedirectToSettingsAlertDialogPreview() {
    FoodYouTheme {
        RedirectToSettingsAlertDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
