package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.infrastructure.di.initKoin
import org.koin.android.ext.koin.androidContext

class FoodYouApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@FoodYouApplication)
        }
    }
}
