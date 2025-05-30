package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MealRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.RecipeRepository
import com.maksimowiczm.foodyou.core.domain.repository.RecipeRepositoryImpl
import com.maksimowiczm.foodyou.core.domain.repository.SearchRepository
import com.maksimowiczm.foodyou.core.domain.repository.SearchRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factory { FoodRepositoryImpl(get(), get(), get()) }.bind<FoodRepository>()
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()
    factory { MeasurementRepositoryImpl(get(), get(), get()) }.bind<MeasurementRepository>()
    factoryOf(::SearchRepositoryImpl).bind<SearchRepository>()
    factory { RecipeRepositoryImpl(get(), get()) }.bind<RecipeRepository>()
}
