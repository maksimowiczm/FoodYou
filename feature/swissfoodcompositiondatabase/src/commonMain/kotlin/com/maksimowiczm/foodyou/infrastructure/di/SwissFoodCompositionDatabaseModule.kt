package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain.ImportSwissDatabaseUseCase
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain.ImportSwissDatabaseUseCaseImpl
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui.SwissFoodCompositionDatabaseViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val swissFoodCompositionDatabaseModule = module {
    factoryOf(::ImportSwissDatabaseUseCaseImpl).bind<ImportSwissDatabaseUseCase>()

    viewModelOf(::SwissFoodCompositionDatabaseViewModel)
}
