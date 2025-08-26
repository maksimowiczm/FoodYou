package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.event.DomainEvent
import kotlinx.datetime.LocalDateTime

data class FoodSearchDomainEvent(val queryType: QueryType.NotBlank.Text, val date: LocalDateTime) :
    DomainEvent
