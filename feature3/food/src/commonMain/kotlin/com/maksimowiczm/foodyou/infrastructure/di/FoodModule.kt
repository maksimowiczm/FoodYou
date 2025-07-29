package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAFacade
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.domain.FoodEventMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodEventMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductMapperImpl
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.feature.food.domain.UpdateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.UpdateProductUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.UpdateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.update.UpdateProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.recipe.MeasureIngredientViewModel
import com.maksimowiczm.foodyou.feature.food.ui.recipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.food.ui.recipe.update.UpdateRecipeViewModel
import com.maksimowiczm.foodyou.feature.food.ui.search.FoodSearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)

    factoryOf(::ProductMapperImpl).bind<ProductMapper>()
    factoryOf(::FoodSearchMapperImpl).bind<FoodSearchMapper>()

    viewModel { (excludedRecipeId: FoodId.Recipe?) ->
        FoodSearchViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            excludedRecipeId = excludedRecipeId
        )
    }

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

    viewModelOf(::CreateRecipeViewModel)
    viewModelOf(::MeasureIngredientViewModel)

    factoryOf(::ObserveRecipeUseCaseImpl).bind<ObserveRecipeUseCase>()

    viewModelOf(::UpdateRecipeViewModel)

    factoryOf(::RemoteProductMapperImpl).bind<RemoteProductMapper>()

    factoryOf(::ObserveFoodUseCaseImpl).bind<ObserveFoodUseCase>()

    factoryOf(::FoodEventMapperImpl).bind<FoodEventMapper>()

    factoryOf(::CreateProductUseCaseImpl).bind<CreateProductUseCase>()

    factoryOf(::UpdateProductUseCaseImpl).bind<UpdateProductUseCase>()

    factoryOf(::CreateRecipeUseCaseImpl).bind<CreateRecipeUseCase>()
    factoryOf(::UpdateRecipeUseCaseImpl).bind<UpdateRecipeUseCase>()
}
