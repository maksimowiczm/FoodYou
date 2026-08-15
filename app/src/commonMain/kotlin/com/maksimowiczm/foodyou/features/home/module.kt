package com.maksimowiczm.foodyou.features.home

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.features.home.integration.HomeProjection
import com.maksimowiczm.foodyou.features.home.ui.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.home() {
    eventHandlerOf(::HomeProjection)

    viewModelOf(::HomeViewModel)
}
