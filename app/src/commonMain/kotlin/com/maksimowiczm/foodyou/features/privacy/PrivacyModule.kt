package com.maksimowiczm.foodyou.features.privacy

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val privacyModule = module { viewModelOf(::PrivacyViewModel) }
