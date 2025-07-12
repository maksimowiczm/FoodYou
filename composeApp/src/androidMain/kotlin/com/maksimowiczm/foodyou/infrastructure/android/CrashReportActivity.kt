package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.ui.crashreport.CrashReportScreen

class CrashReportActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorMessage = intent.getStringExtra("report").toString()

        setContent {
            FoodYouTheme {
                CrashReportScreen(
                    message = errorMessage
                )
            }
        }
    }
}
