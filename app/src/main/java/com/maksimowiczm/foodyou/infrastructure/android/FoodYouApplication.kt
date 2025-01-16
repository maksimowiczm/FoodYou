package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.infrastructure.di.databaseModule
import com.maksimowiczm.foodyou.infrastructure.di.diaryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FoodYouApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                databaseModule,
                diaryModule,
                dataStoreModule
            )
        }
    }
}
