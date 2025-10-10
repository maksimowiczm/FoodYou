package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.common.ProfileId

@Immutable
data class ProfileUiState(val id: ProfileId, val name: String, val avatar: UiProfileAvatar)
