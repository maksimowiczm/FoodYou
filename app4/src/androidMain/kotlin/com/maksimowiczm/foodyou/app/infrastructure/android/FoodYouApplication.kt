package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.analytics.application.AppLaunchCommand
import com.maksimowiczm.foodyou.analytics.application.AppLaunchCommandHandler
import com.maksimowiczm.foodyou.app.di.initKoin
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

        coroutineScope.launch {
            koin
                .get<AppLaunchCommandHandler>()
                .execute(
                    AppLaunchCommand(
                        localAccountId = LocalAccountId.DEFAULT,
                        versionName = koin.get<AppConfig>().versionName,
                    )
                )
        }
    }
}
