package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.database.exportcsvproducts.presentation.ExportProductsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val FeatureSettingsDatabaseExportCsvProductsModule = module {
    viewModelOf(::ExportProductsViewModel)
}
