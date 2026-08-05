package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId

@Immutable
data class ProfileUiState(val id: ProfileId, val name: String, val avatar: Profile.Avatar)
