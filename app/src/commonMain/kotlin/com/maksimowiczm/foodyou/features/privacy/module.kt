package com.maksimowiczm.foodyou.features.privacy

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.privacy() {
    viewModelOf(::PrivacyViewModel)
}
