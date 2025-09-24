package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouLogger
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouNetworkConfig
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.csv.csvModule
import com.maksimowiczm.foodyou.common.infrastructure.datastore.dataStoreModule
import com.maksimowiczm.foodyou.common.infrastructure.inmemory.inMemoryModule
import com.maksimowiczm.foodyou.common.infrastructure.koin.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.infrastructure.system.systemModule
import com.maksimowiczm.foodyou.common.log.Logger
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule(applicationCoroutineScope: CoroutineScope) = module {
    factoryOf(::FoodYouConfig).bind<AppConfig>()
    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
    single { FoodYouLogger }.bind<Logger>()
    applicationCoroutineScope { applicationCoroutineScope }

    csvModule()
    dataStoreModule()
    inMemoryModule()
    roomModule()
    systemModule()
}
