package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepositoryImpl

object OpenSourceAddFoodFeature : AddFoodFeature(
    addFoodRepository = {
        factory {
            AddFoodRepositoryImpl(
                addFoodDatabase = get(),
                productDatabase = get(),
                productRemoteMediatorFactory = get()
            )
        }
    }
)
