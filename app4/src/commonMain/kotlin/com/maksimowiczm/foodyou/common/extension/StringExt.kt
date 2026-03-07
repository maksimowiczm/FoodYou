package com.maksimowiczm.foodyou.common.extension

fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
