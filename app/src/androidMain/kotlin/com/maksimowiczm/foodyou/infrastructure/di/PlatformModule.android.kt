package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule = module {
    factoryOf(::SystemInfoRepository)
    factoryOf(::StringFormatRepository)

    // -- Language
    viewModelOf(::LanguageViewModel)
}
