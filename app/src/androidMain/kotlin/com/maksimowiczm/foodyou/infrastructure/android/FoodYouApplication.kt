package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import android.content.Intent
import android.os.Build
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.infrastructure.di.initKoin
import org.koin.android.ext.koin.androidContext

class FoodYouApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@FoodYouApplication)
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            handleUncaughtException(e)
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun handleUncaughtException(e: Throwable) {
        val intent = Intent(this, CrashReportActivity::class.java)

        val report = buildString {
            appendLine("Version: ${BuildConfig.VERSION_NAME}")
            appendLine("Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine(e.stackTraceToString())
        }

        intent.putExtra("report", report)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
    }
}
