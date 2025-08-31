package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.database.importcsvproducts.presentation.ImportCsvProductsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val featureSettingsDatabaseImportCsvProductsModule = module {
    viewModelOf(::ImportCsvProductsViewModel)
}
