package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.OpenSourceAppConfig
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.CrashReportScreen
import org.koin.android.ext.android.get

class CrashReportActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorMessage = intent.getStringExtra("report").toString()
        val appConfig: OpenSourceAppConfig = get()

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
