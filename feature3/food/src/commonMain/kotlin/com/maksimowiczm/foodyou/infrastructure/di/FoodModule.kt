package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAFacade
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.update.UpdateProductScreenViewModel
import com.maksimowiczm.foodyou.feature.food.ui.search.FoodSearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductScreenViewModel)

    factoryOf(::ProductMapperImpl).bind<ProductMapper>()
    factoryOf(::FoodSearchMapperImpl).bind<FoodSearchMapper>()

    viewModelOf(::FoodSearchViewModel)

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::OpenFoodFactsFacade)

    factoryOf(::USDAFacade)
    factoryOf(::USDAProductMapper)

    viewModel { (url: String?) ->
        DownloadProductViewModel(
            text = url,
            requestFactory = get()
        )
    }
}
