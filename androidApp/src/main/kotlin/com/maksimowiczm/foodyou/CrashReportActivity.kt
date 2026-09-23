package com.maksimowiczm.foodyou

import android.content.ClipData
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.toClipEntry
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.features.crash.CrashReportScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CrashReportActivity : AppCompatActivity() {
    val appConfig: FoodYouConfig by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val errorMessage = intent.getStringExtra("report").toString()

        setContent {
            val isDark = isSystemInDarkTheme()
            val colorScheme =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (isDark) dynamicDarkColorScheme(this) else dynamicLightColorScheme(this)
                } else {
                    if (isDark) darkColorScheme() else lightColorScheme()
                }

            MaterialExpressiveTheme(colorScheme = colorScheme) {
                val clipboardManager = LocalClipboard.current
                val uriHandler = LocalUriHandler.current

                CrashReportScreen(
                    message = errorMessage,
                    onCopyAndSend = {
                        lifecycleScope.launch {
                            clipboardManager.setClipEntry(
                                ClipData.newPlainText("error", errorMessage).toClipEntry()
                            )
                            uriHandler.openUri(appConfig.bugReportUri)
                        }
                    },
                )
            }
        }
    }
}
