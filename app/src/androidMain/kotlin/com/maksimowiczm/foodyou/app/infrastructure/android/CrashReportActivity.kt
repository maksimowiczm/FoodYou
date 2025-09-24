package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import com.maksimowiczm.foodyou.app.ui.crash.CrashReportScreen
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.common.config.AppConfig
import org.koin.android.ext.android.get

class CrashReportActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorMessage = intent.getStringExtra("report").toString()
        val appConfig: AppConfig = get()

        setContent {
            FoodYouTheme {
                CrashReportScreen(
                    message = errorMessage,
                    issueTrackerUrl = appConfig.issueTrackerUrl,
                )
            }
        }
    }
}
