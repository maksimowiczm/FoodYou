package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.maksimowiczm.foodyou.R
import org.koin.compose.koinInject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraBarcodeScannerScreen(
    onBarcodeScan: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraBarcodeScannerViewModel = koinInject()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val cameraPermissionRequests by viewModel.cameraPermissionRequests.collectAsStateWithLifecycle()
    val isGranted by remember(cameraPermissionState) {
        derivedStateOf { cameraPermissionState.status.isGranted }
    }
    LaunchedEffect(isGranted) {
        if (isGranted) {
            viewModel.onPermissionGranted()
        }
    }

    val context = LocalContext.current
    val onRequestPermission = {
        when (cameraPermissionRequests) {
            null, 0, 1 -> {
                viewModel.onPermissionRequested()
                cameraPermissionState.launchPermissionRequest()
            }

            else -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    }

    val hapticFeedback = LocalHapticFeedback.current

    CameraBarcodeScannerScreen(
        isGranted = isGranted,
        shouldShowRationale = cameraPermissionState.status.shouldShowRationale,
        cameraPermissionRequests = cameraPermissionRequests,
        onRequestPermission = onRequestPermission,
        onBarcodeScan = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            onBarcodeScan(it)
        },
        modifier = modifier
    )
}

@Composable
private fun CameraBarcodeScannerScreen(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
    cameraPermissionRequests: Int?,
    onRequestPermission: () -> Unit,
    onBarcodeScan: (String) -> Unit,
    modifier: Modifier = Modifier,
    barcodeScannerScreen: BarcodeScannerScreen = koinInject()
) {
    if (isGranted) {
        barcodeScannerScreen(
            onBarcodeScan = onBarcodeScan,
            modifier = modifier
        )
    } else {
        CameraRequestPermissionScreen(
            shouldShowRationale = shouldShowRationale,
            willRedirectToSettings = cameraPermissionRequests?.let { it > 1 } ?: false,
            onRequestPermission = onRequestPermission,
            modifier = modifier
        )
    }
}

@Composable
private fun CameraRequestPermissionScreen(
    shouldShowRationale: Boolean,
    willRedirectToSettings: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onRequestPermission)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = if (willRedirectToSettings) {
                    stringResource(R.string.neutral_camera_request_redirect_to_settings)
                } else if (shouldShowRationale) {
                    stringResource(R.string.neutral_camera_request_rationale)
                } else {
                    stringResource(R.string.neutral_camera_request)
                },
                textAlign = TextAlign.Center
            )
            Text(
                text = if (willRedirectToSettings) {
                    stringResource(R.string.action_tap_to_open_settings)
                } else {
                    stringResource(R.string.action_tap_to_allow_access)
                }
            )
        }
    }
}
