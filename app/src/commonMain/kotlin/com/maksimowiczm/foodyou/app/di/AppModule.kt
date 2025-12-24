package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouLogger
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.common.config.AppConfig
import com.maksimowiczm.foodyou.common.config.NetworkConfig
import com.maksimowiczm.foodyou.common.infrastructure.auth.authModule
import com.maksimowiczm.foodyou.common.infrastructure.crypto.cryptoModule
import com.maksimowiczm.foodyou.common.infrastructure.csv.csvModule
import com.maksimowiczm.foodyou.common.infrastructure.datastore.dataStoreModule
import com.maksimowiczm.foodyou.common.infrastructure.inmemory.inMemoryModule
import com.maksimowiczm.foodyou.common.infrastructure.koin.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.infrastructure.system.systemModule
import com.maksimowiczm.foodyou.common.log.Logger
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun appModule(applicationCoroutineScope: CoroutineScope) = module {
    factoryOf(::FoodYouConfig).binds(arrayOf(AppConfig::class, NetworkConfig::class))
    single { FoodYouLogger }.bind<Logger>()
    applicationCoroutineScope { applicationCoroutineScope }

    authModule()
    cryptoModule()
    csvModule()
    dataStoreModule()
    inMemoryModule()
    roomModule()
    systemModule()
}
