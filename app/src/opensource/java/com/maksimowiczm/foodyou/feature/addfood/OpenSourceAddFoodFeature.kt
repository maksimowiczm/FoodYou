package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.feature.camera.OpenSourceCameraFeature
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsFeature

object OpenSourceAddFoodFeature : AddFoodFeature(
    addFoodRepository = {
        factory {
            AddFoodRepositoryImpl(
                addFoodDatabase = get(),
                productDatabase = get(),
                productRemoteMediatorFactory = get()
            )
        }
    },
    cameraFeature = OpenSourceCameraFeature,
    productFeature = OpenFoodFactsFeature
)
