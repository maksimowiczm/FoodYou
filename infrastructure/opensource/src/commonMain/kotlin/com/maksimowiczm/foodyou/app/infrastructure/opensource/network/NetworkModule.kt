package com.maksimowiczm.foodyou.app.infrastructure.opensource.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.NetworkConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.networkModule() {
    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
}
