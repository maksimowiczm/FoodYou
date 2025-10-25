package com.maksimowiczm.foodyou.app.ui.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar

@Stable
data class ProfileUiState(
    val avatar: UiProfileAvatar = DEFAULT_AVATAR,
    val nameTextState: TextFieldState = TextFieldState(),
    val isLocked: Boolean = false,
    private val defaultAvatar: UiProfileAvatar = DEFAULT_AVATAR,
    private val defaultName: String = "",
) {
    val isValid by derivedStateOf { nameTextState.text.isNotBlank() }

    val isModified by derivedStateOf {
        nameTextState.text != defaultName || avatar != defaultAvatar
    }

    companion object {
        val DEFAULT_AVATAR = UiProfileAvatar.PERSON
    }
}
