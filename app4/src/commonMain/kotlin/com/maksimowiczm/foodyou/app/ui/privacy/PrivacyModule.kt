package com.maksimowiczm.foodyou.app.ui.privacy

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val privacyModule = module { viewModelOf(::PrivacyViewModel) }
