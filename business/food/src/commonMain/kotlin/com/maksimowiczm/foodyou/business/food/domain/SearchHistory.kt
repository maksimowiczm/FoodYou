package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.datetime.LocalDateTime

data class SearchHistory(val date: LocalDateTime, val query: String)
