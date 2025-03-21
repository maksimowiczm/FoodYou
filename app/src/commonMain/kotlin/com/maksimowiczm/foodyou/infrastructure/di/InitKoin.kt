package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.Feature
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(features: List<Feature>, config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        databaseModule,
        dataStoreModule
    )

    val featureModules = features.map { feature ->
        feature.module
    }

    modules(featureModules)
}
