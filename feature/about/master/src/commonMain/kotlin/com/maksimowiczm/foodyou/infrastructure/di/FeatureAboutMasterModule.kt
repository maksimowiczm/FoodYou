package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.feature.about.master.presentation.Changelog
import com.maksimowiczm.foodyou.feature.about.master.presentation.PreviewReleaseDialogViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureAboutMasterModule = module {
    factoryOf(::Changelog)
    viewModelOf(::PreviewReleaseDialogViewModel)
    viewModel {
        PreviewReleaseDialogViewModel(
            changelog = get(),
            settingsRepository = get(named(Settings::class.qualifiedName!!)),
            savedStateHandle = get(),
        )
    }
}
