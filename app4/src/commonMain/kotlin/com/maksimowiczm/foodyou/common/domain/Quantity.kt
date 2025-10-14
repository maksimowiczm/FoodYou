package com.maksimowiczm.foodyou.common.domain

sealed interface Quantity {
    data class Weight(val weight: com.maksimowiczm.foodyou.common.domain.Weight) : Quantity

    data class Volume(val volume: com.maksimowiczm.foodyou.common.domain.Volume) : Quantity
}
