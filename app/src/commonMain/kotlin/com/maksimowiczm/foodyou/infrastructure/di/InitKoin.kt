package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.coreModule
import com.maksimowiczm.foodyou.feature.about.aboutModule
import com.maksimowiczm.foodyou.feature.calendar.calendarModule
import com.maksimowiczm.foodyou.feature.language.languageModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        dataStoreModule,
        coreModule
    )

    modules(
        aboutModule,
        calendarModule,
        languageModule
    )
}
