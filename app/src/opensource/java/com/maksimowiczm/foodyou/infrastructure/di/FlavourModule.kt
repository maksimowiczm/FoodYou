package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.data.AndroidLinkHandler
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.data.LinkHandler
import com.maksimowiczm.foodyou.data.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.data.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.feature.legacy.camera.ui.zxingCameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.AboutSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui.CountryFlag
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui.OpenFoodFactsSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui.flagCdnCountryFlag
import com.maksimowiczm.foodyou.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.network.ProductRemoteMediatorFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val flavourModule = module {
    factoryOf(::AndroidLinkHandler).bind<LinkHandler>()
    viewModelOf(::AboutSettingsViewModel)

    viewModelOf(::OpenFoodFactsSettingsViewModel)
    factoryOf(::OpenFoodFactsSettingsRepositoryImpl).bind<OpenFoodFactsSettingsRepository>()
    singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()
    factory { flagCdnCountryFlag }.bind<CountryFlag>()

    // -- Legacy part --

    // Legacy Add Food Feature
    factory {
        AddFoodRepositoryImpl(
            addFoodDao = get(),
            productDao = get(),
            productRemoteMediatorFactory = get()
        )
    }.bind<AddFoodRepository>()
    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    // Legacy Open Source Camera Feature
    factory { zxingCameraBarcodeScannerScreen }

    // Legacy Open Source Diary Feature
    factoryOf(::DiaryRepositoryImpl).bind<DiaryRepository>()
}
