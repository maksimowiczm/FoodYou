package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.master.presentation.Changelog
import com.maksimowiczm.foodyou.feature.about.master.presentation.PreviewReleaseDialogViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureAboutMasterModule = module {
    factoryOf(::Changelog)
    viewModelOf(::PreviewReleaseDialogViewModel)
}
