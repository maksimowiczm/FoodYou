package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import android.content.Intent
import android.os.Build
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.business.settings.domain.AppLaunchEvent
import com.maksimowiczm.foodyou.infrastructure.di.initKoin
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.event.EventBus
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class FoodYouApplication : Application() {

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("FoodYouApplication"))
    }

    override fun onCreate() {
        super.onCreate()

        initKoin(coroutineScope) { androidContext(this@FoodYouApplication) }
        publishLaunchEvent()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            handleUncaughtException(e)
            defaultHandler?.uncaughtException(t, e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun publishLaunchEvent() {
        val dateProvider: DateProvider by inject()
        val eventBus: EventBus by inject()

        val event = AppLaunchEvent(timestamp = dateProvider.nowInstant())
        eventBus.publish(event)
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
