package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.master.presentation.Changelog
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featureAboutMasterModule = module { factoryOf(::Changelog) }
