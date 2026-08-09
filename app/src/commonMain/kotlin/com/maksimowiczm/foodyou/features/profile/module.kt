package com.maksimowiczm.foodyou.features.profile

import com.maksimowiczm.foodyou.features.profile.add.AddProfileViewModel
import com.maksimowiczm.foodyou.features.profile.edit.EditProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.profile() {
    viewModelOf(::AddProfileViewModel)
    viewModelOf(::EditProfileViewModel)
}
