package com.maksimowiczm.foodyou.app.infrastructure.shared

import com.maksimowiczm.foodyou.app.business.shared.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.app.infrastructure.shared.csv.csvModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.date.dateModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.event.eventModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary.foodDiaryModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.translation.translationModule
import kotlinx.coroutines.CoroutineScope
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val systemDetails: Module.() -> KoinDefinition<out SystemDetails>

fun infrastructureSharedModule(applicationCoroutineScope: CoroutineScope) = module {
    applicationCoroutineScope { applicationCoroutineScope }

    systemDetails()

    csvModule()
    dateModule()
    eventModule()
    foodDiaryModule()
    translationModule()
}
