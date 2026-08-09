package com.maksimowiczm.foodyou

import android.os.Bundle
import androidx.compose.ui.platform.LocalUriHandler
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.capabilities.theme.FoodYouTheme
import com.maksimowiczm.foodyou.features.crash.CrashReportScreen
import com.maksimowiczm.foodyou.shared.ui.utility.LocalClipboardManager
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
                        clipboardManager.copy("Report", errorMessage)
                        uriHandler.openUri(appConfig.bugReportUri)
                    },
                )
            }
        }
    }
}
