package com.maksimowiczm.foodyou

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import com.maksimowiczm.foodyou.analytics.application.AnalyticsService
import com.maksimowiczm.foodyou.analytics.domain.recordAppLaunch
import com.maksimowiczm.foodyou.app.di.AppModule
import com.maksimowiczm.foodyou.app.di.initKoin
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import com.maksimowiczm.foodyou.common.event.LoggingEventBus
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

class FoodYouApplication : Application() {
    private val coroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("FoodYouApplication"))

    override fun onCreate() {
        super.onCreate()

        val koin =
            initKoin(
                    appModule =
                        AppModule(
                            foodYouConfig = { single { FoodYouConfig(BuildConfig.VERSION_NAME) } }
                        )
                ) {
                    androidContext(this@FoodYouApplication)
                    modules(
                        module {
                            applicationCoroutineScope { coroutineScope }
                            if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                                single { LoggingEventBus(get<InMemoryEventBus>(), get()) }
                                    .bind<EventBus>()
                            }
                        }
                    )
                }
                .koin

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            handleUncaughtException(e)
            defaultHandler?.uncaughtException(t, e)
        }

        coroutineScope.launch {
            koin.get<AnalyticsService>().update {
                recordAppLaunch(versionName = BuildConfig.VERSION_NAME)
            }
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
