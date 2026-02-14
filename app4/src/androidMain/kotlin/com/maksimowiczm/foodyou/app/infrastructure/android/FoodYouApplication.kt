package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.Application
import android.content.Intent
import android.os.Build
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.analytics.application.AppLaunchUseCase
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.app.di.initKoin
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class FoodYouApplication : Application() {
    private val coroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("FoodYouApplication"))

    override fun onCreate() {
        super.onCreate()

        val koin =
            initKoin {
                    androidContext(this@FoodYouApplication)
                    modules(module { applicationCoroutineScope { coroutineScope } })
                }
                .koin

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            handleUncaughtException(e)
            defaultHandler?.uncaughtException(t, e)
        }

        coroutineScope.launch {
            val accountManager = koin.get<AppAccountManager>()
            val accountId = accountManager.observeAppAccountId().first() ?: return@launch
            koin.get<AppLaunchUseCase>().execute(accountId)
        }
    }

    private fun handleUncaughtException(e: Throwable) {
        val intent = Intent(this, CrashReportActivity::class.java)

        val report = buildString {
            appendLine("${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}")
            appendLine("Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})")
            appendLine("${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine()
            appendLine(e.stackTraceToString())
        }

        intent.putExtra("report", report)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
    }
}
