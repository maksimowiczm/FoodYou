package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.aboutModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        dataStoreModule
    )

    modules(
        aboutModule
    )
}
