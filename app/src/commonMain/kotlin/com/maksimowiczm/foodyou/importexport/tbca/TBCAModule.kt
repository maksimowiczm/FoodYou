package com.maksimowiczm.foodyou.importexport.tbca

import com.maksimowiczm.foodyou.app.ui.database.tbca.TBCAViewModel
import com.maksimowiczm.foodyou.importexport.tbca.domain.ImportTBCAUseCase
import com.maksimowiczm.foodyou.importexport.tbca.domain.ImportTBCAUseCaseImpl
import com.maksimowiczm.foodyou.importexport.tbca.domain.TBCARepository
import com.maksimowiczm.foodyou.importexport.tbca.infrastructure.ComposeTBCARepository
import com.maksimowiczm.foodyou.importexport.tbca.infrastructure.TBCAMapper
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for TBCA (Brazilian Food Composition Table) dependency injection.
 */
val importExportTBCAModule = module {
    // Json instance for TBCA parsing
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // Repository
    single<TBCARepository> {
        ComposeTBCARepository(
            mapper = get(),
            json = get(),
        )
    }

    // Mapper
    singleOf(::TBCAMapper)

    // Use Case
    factoryOf(::ImportTBCAUseCaseImpl).bind<ImportTBCAUseCase>()

    // ViewModel
    factoryOf(::TBCAViewModel)
}
