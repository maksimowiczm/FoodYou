package com.maksimowiczm.foodyou.app.ui.profile

import com.maksimowiczm.foodyou.app.ui.profile.add.AddProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module { viewModelOf(::AddProfileViewModel) }
