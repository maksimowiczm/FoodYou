package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.domain.FoodRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.MealMapper
import com.maksimowiczm.foodyou.core.domain.MealMapperImpl
import com.maksimowiczm.foodyou.core.domain.MealRepository
import com.maksimowiczm.foodyou.core.domain.MealRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.MeasurementMapperImpl
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.ProductMapper
import com.maksimowiczm.foodyou.core.domain.ProductMapperImpl
import com.maksimowiczm.foodyou.core.domain.RecipeRepository
import com.maksimowiczm.foodyou.core.domain.RecipeRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factory { ProductMapperImpl }.bind<ProductMapper>()
    factory { MeasurementMapperImpl }.bind<MeasurementMapper>()
    factory { MealMapperImpl }.bind<MealMapper>()

    factoryOf(::FoodRepositoryImpl).bind<FoodRepository>()
    factoryOf(::RecipeRepositoryImpl).bind<RecipeRepository>()
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()
    factoryOf(::MeasurementRepositoryImpl).bind<MeasurementRepository>()
}
