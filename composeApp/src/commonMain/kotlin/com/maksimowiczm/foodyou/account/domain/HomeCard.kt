package com.maksimowiczm.foodyou.account.domain

import kotlinx.serialization.Serializable

@Serializable
enum class HomeCard {
    Calendar;

    companion object {
        val defaultOrder = listOf(Calendar)
    }
}
