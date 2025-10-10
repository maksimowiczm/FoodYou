package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.account.di.accountModule
import com.maksimowiczm.foodyou.analytics.di.analyticsModule
import com.maksimowiczm.foodyou.app.infrastructure.config.configModule
import com.maksimowiczm.foodyou.app.infrastructure.datastore.dataStoreModule
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.app.ui.appUiModule
import com.maksimowiczm.foodyou.app.ui.common.theme.commonThemeModule
import com.maksimowiczm.foodyou.app.ui.home.homeModule
import com.maksimowiczm.foodyou.app.ui.onboarding.onboardingModule
import com.maksimowiczm.foodyou.common.clock.di.clockModule
import com.maksimowiczm.foodyou.common.event.di.inMemoryEventBusModule
import com.maksimowiczm.foodyou.device.di.deviceModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication = startKoin {
    // App modules
    modules(dataStoreModule, roomModule, configModule)

    // Common modules
    modules(clockModule, inMemoryEventBusModule)

    // Feature modules
    modules(accountModule, analyticsModule, deviceModule)

    // Ui modules
    modules(commonThemeModule, onboardingModule, appUiModule, homeModule)

    config?.invoke(this)
}
