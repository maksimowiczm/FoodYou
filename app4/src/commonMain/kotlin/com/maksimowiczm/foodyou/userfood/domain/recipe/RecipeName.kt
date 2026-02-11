package com.maksimowiczm.foodyou.userfood.domain.recipe

import kotlin.jvm.JvmInline

@JvmInline
value class RecipeName(val value: String) {
    init {
        require(value.isNotBlank()) { "Recipe name cannot be blank" }
    }
}
