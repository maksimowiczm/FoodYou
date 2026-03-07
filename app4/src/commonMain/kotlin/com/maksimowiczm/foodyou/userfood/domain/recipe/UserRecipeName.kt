package com.maksimowiczm.foodyou.userfood.domain.recipe

import kotlin.jvm.JvmInline

@JvmInline
value class UserRecipeName(val value: String) {
    init {
        require(value.isNotBlank()) { "Recipe name cannot be blank" }
    }
}
