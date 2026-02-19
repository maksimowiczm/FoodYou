package com.maksimowiczm.foodyou.app.ui.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.saveable.jsonSaver

@Stable
internal class ProfileFormState(
    val nameTextState: TextFieldState,
    avatarState: MutableState<UiProfileAvatar>,
    private val defaultName: String,
    private val defaultAvatar: UiProfileAvatar,
) {
    var avatar: UiProfileAvatar by avatarState

    val isValid by derivedStateOf { nameTextState.text.isNotBlank() }

    val isModified by derivedStateOf {
        nameTextState.text != defaultName || avatar != defaultAvatar
    }

    companion object {
        val DEFAULT_AVATAR = UiProfileAvatar.Predefined(UiProfileAvatar.Predefined.Variant.PERSON)
    }
}

@Composable
internal fun rememberProfileFormState(
    defaultName: String = "",
    defaultAvatar: UiProfileAvatar = ProfileFormState.DEFAULT_AVATAR,
): ProfileFormState {
    val nameTextFieldState = rememberTextFieldState(defaultName)
    val avatarState = rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf(defaultAvatar) }

    return remember(nameTextFieldState, avatarState, defaultName, defaultAvatar) {
        ProfileFormState(
            nameTextState = nameTextFieldState,
            avatarState = avatarState,
            defaultName = defaultName,
            defaultAvatar = defaultAvatar,
        )
    }
}
