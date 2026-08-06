package com.maksimowiczm.foodyou.features.profile

import com.maksimowiczm.foodyou.features.profile.add.AddProfileViewModel
import com.maksimowiczm.foodyou.features.profile.edit.EditProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::AddProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}
