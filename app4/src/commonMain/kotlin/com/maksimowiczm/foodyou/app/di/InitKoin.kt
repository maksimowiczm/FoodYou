package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.account.di.accountModule
import com.maksimowiczm.foodyou.analytics.di.analyticsModule
import com.maksimowiczm.foodyou.app.infrastructure.dataStoreModule
import com.maksimowiczm.foodyou.common.clock.di.clockModule
import com.maksimowiczm.foodyou.common.event.di.inMemoryEventBusModule
import com.maksimowiczm.foodyou.device.di.deviceModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    // App modules
    modules(dataStoreModule)

    // Common modules
    modules(clockModule, inMemoryEventBusModule)

    // Feature modules
    modules(accountModule, analyticsModule, deviceModule)

    config?.invoke(this)
}
