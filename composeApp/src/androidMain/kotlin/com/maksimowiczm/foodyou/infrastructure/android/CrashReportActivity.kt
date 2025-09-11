package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.AppConfig
import com.maksimowiczm.foodyou.shared.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.CrashReportScreen
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
