package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.ProfileId
import kotlin.uuid.Uuid

class Profile(val id: ProfileId, val name: String, val avatar: Avatar) {
    companion object {
        fun new(name: String, avatar: Avatar): Profile {
            val id = Uuid.random()
            return Profile(id = ProfileId(id.toString()), name = name, avatar = avatar)
        }
    }

    enum class Avatar {
        PERSON,
        WOMAN,
        MAN,
        ENGINEER,
    }
}
