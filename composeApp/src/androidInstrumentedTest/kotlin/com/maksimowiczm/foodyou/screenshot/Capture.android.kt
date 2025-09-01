package com.maksimowiczm.foodyou.screenshot

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidClipboardManager
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.shared.ui.utils.DateFormatterProvider

@OptIn(ExperimentalTestApi::class)
actual suspend fun ComposeUiTest.capture(screenshot: Screenshot) {
    var context: Context? = null

    setContent {
        context = LocalContext.current
        ClipboardManagerProvider(AndroidClipboardManager(context)) {
            DateFormatterProvider(AndroidDateFormatter(context)) {
                Box(
                    Modifier.Companion.consumeWindowInsets(WindowInsets.Companion.systemBars)
                        .consumeWindowInsets(WindowInsets.Companion.displayCutout)
                        .padding(vertical = 8.dp)
                ) {
                    screenshot.Content()
                }
            }
        }
    }

    awaitIdle()

    val bitmap = onRoot().captureToImage().asAndroidBitmap()
    saveScreenshotToPictures(context!!, bitmap, screenshot.name)
}

private fun saveScreenshotToPictures(context: Context, bitmap: Bitmap, name: String) {
    val values =
        ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/${context.packageName}",
            )
        }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }
}
