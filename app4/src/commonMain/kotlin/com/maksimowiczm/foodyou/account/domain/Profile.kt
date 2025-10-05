package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.ProfileId
import kotlin.uuid.Uuid

class Profile(val id: ProfileId, val name: String) {
    companion object {
        fun new(name: String): Profile {
            val id = Uuid.random()
            return Profile(id = ProfileId(id.toString()), name = name)
        }
    }
}
