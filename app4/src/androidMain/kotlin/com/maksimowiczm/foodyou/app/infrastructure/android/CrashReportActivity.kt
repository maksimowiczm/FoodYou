package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import androidx.compose.ui.platform.LocalUriHandler
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.ui.common.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.app.ui.crash.CrashReportScreen
import org.koin.android.ext.android.inject

class CrashReportActivity : FoodYouAbstractActivity() {
    val appConfig: AppConfig by inject()

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
