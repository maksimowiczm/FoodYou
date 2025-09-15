package com.maksimowiczm.foodyou.app.infrastructure.opensource.network

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.OpenSourceNetworkConfig
import com.maksimowiczm.foodyou.app.business.shared.domain.config.NetworkConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.binds

internal fun Module.networkModule() {
    factoryOf(::FoodYouNetworkConfig)
        .binds(arrayOf(OpenSourceNetworkConfig::class, NetworkConfig::class))
}
