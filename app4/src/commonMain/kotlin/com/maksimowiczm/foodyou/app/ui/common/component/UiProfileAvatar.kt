package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person3
import androidx.compose.material.icons.outlined.Person4
import androidx.compose.ui.graphics.vector.ImageVector
import com.maksimowiczm.foodyou.account.domain.Profile

enum class UiProfileAvatar {
    PERSON,
    WOMAN,
    MAN,
    ENGINEER;

    fun toImageVector(): ImageVector =
        when (this) {
            PERSON -> Icons.Outlined.Person
            WOMAN -> Icons.Outlined.Person3
            MAN -> Icons.Outlined.Person4
            ENGINEER -> Icons.Outlined.Engineering
        }
}

fun UiProfileAvatar.toProfileAvatar(): Profile.Avatar =
    when (this) {
        UiProfileAvatar.PERSON -> Profile.Avatar.PERSON
        UiProfileAvatar.WOMAN -> Profile.Avatar.WOMAN
        UiProfileAvatar.MAN -> Profile.Avatar.MAN
        UiProfileAvatar.ENGINEER -> Profile.Avatar.ENGINEER
    }
