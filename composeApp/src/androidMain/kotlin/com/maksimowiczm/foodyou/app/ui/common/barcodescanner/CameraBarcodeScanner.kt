package com.maksimowiczm.foodyou.app.ui.common.barcodescanner

import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LocalLifecycleOwner
import foodyou.app.generated.resources.*
import java.util.concurrent.Executors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val scanFraction = .8f

@Composable
internal fun CameraBarcodeScanner(onBarcodeScan: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val motionScheme = MaterialTheme.motionScheme

    val cameraProvider = remember { ProcessCameraProvider.getInstance(context).get() }
    DisposableEffect(cameraProvider) { onDispose { cameraProvider.unbindAll() } }
    var camera by remember { mutableStateOf<Camera?>(null) }
    val hasTorch = remember(camera) { camera?.cameraInfo?.hasFlashUnit() ?: false }
    var torchOn by remember { mutableStateOf(false) }

    val cameraSelector = remember {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }

    val preview = remember { Preview.Builder().build() }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(analysisExecutor) { onDispose { analysisExecutor.shutdown() } }
    val animatableCertainty = remember { Animatable(0f) }
    val analyzer = remember {
        ConfirmingBarcodeAnalyzer(
            scanFraction = scanFraction,
            strategy =
                CombinedBarcodeConfirmationStrategy(
                    ConsecutiveBarcodeConfirmationStrategy(confirmationsRequired = 10),
                    BufferBarcodeConfirmationStrategy(confirmationsRequired = 5, bufferSize = 20),
                ),
            onBarcode = onBarcodeScan,
            onCertainty = {
                scope.launch { animatableCertainty.animateTo(it, motionScheme.fastEffectsSpec()) }
            },
        )
    }

    Box(modifier) {
        AndroidView(
            factory = { factoryContext ->
                PreviewView(factoryContext).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    preview.surfaceProvider = surfaceProvider
                }
            },
            update = { view ->
                if (cameraProvider == null) {
                    return@AndroidView
                }

                cameraProvider.unbindAll()
                imageAnalysis.setAnalyzer(analysisExecutor, analyzer)
                val viewPort = view.viewPort
                camera =
                    if (viewPort != null) {
                        val useCaseGroup =
                            UseCaseGroup.Builder()
                                .setViewPort(viewPort)
                                .addUseCase(preview)
                                .addUseCase(imageAnalysis)
                                .build()

                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup)
                    } else {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis,
                        )
                    }
            },
        )
        Canvas(
            Modifier.matchParentSize().graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val scanSize = minOf(canvasWidth, canvasHeight) * scanFraction
            val scanLeft = (canvasWidth - scanSize) / 2f
            val scanTop = (canvasHeight - scanSize) / 2f
            val cornerRadius = CornerRadius(24.dp.toPx())

            val color = lerp(colorScheme.scrim, colorScheme.primary, animatableCertainty.value)
            val alpha = lerp(.5f, 1f, animatableCertainty.value)
            drawRect(color = colorScheme.scrim.copy(alpha), size = size)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(scanLeft, scanTop),
                size = Size(scanSize, scanSize),
                cornerRadius = cornerRadius,
                blendMode = BlendMode.Clear,
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(scanLeft, scanTop),
                size = Size(scanSize, scanSize),
                cornerRadius = cornerRadius,
                style = Stroke(width = 8.dp.toPx()),
            )
        }

        if (hasTorch) {
            FlashlightButton(
                enabled = torchOn,
                onClick = {
                    val newState = !torchOn
                    camera?.cameraControl?.enableTorch(newState)
                    torchOn = newState
                },
                modifier = Modifier.align(Alignment.BottomEnd).safeGesturesPadding().zIndex(10f),
            )
        }
    }
}

@Composable
private fun FlashlightButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background by
        animateColorAsState(
            if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    val content by
        animateColorAsState(
            if (enabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )

    FilledIconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.shapes(),
        modifier = modifier,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = background,
                contentColor = content,
            ),
    ) {
        Icon(
            imageVector = if (enabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription =
                if (enabled) stringResource(Res.string.action_disable_camera_flash)
                else stringResource(Res.string.action_enable_camera_flash),
        )
    }
}
