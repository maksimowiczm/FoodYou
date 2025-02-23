package com.maksimowiczm.foodyou.feature.diary.ui

object SharedTransitionKeys {
    data class Meal(val id: Long, val epochDay: Int) {
        data class Title(val id: Long, val epochDay: Int)
    }
}
