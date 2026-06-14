package com.maksimowiczm.foodyou.app.ui

import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelectorImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appUiModule = module {
    viewModelOf(::AppViewModel)
    factoryOf(::FoodNameSelectorImpl).bind<FoodNameSelector>()
}
