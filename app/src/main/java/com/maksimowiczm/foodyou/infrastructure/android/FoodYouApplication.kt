package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.infrastructure.di.addFoodModule
import com.maksimowiczm.foodyou.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.infrastructure.di.databaseModule
import com.maksimowiczm.foodyou.infrastructure.di.diaryModule
import com.maksimowiczm.foodyou.infrastructure.di.flavourModule
import com.maksimowiczm.foodyou.infrastructure.di.productsModule
import com.maksimowiczm.foodyou.infrastructure.di.settingsModule
import com.maksimowiczm.foodyou.infrastructure.di.systemModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FoodYouApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                databaseModule,
                dataStoreModule,
                diaryModule,
                addFoodModule,
                productsModule,
                flavourModule,
                systemModule,
                settingsModule
            )
        }
    }
}
