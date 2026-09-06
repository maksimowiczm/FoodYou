package com.maksimowiczm.foodyou

import android.os.Bundle
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.capabilities.theme.FoodYouTheme
import com.maksimowiczm.foodyou.features.crash.CrashReportScreen
import org.koin.android.ext.android.inject

class CrashReportActivity : FoodYouAbstractActivity() {
    val appConfig: FoodYouConfig by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorMessage = intent.getStringExtra("report").toString()

        setContent {
            FoodYouTheme {
                val clipboardManager = LocalClipboardManager.current
                val uriHandler = LocalUriHandler.current

                CrashReportScreen(
                    message = errorMessage,
                    onCopyAndSend = {
                        clipboardManager.setText(AnnotatedString(errorMessage))
                        uriHandler.openUri(appConfig.bugReportUri)
                    },
                )
            }
        }
    }
}
