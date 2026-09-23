package com.maksimowiczm.foodyou.capabilities

import com.maksimowiczm.foodyou.capabilities.foodbrowsing.foodBrowsing
import com.maksimowiczm.foodyou.capabilities.fooddetails.foodDetails
import com.maksimowiczm.foodyou.capabilities.theme.theme
import org.koin.dsl.module

val capabilitiesModule = module {
    foodBrowsing()
    foodDetails()
    theme()
}
