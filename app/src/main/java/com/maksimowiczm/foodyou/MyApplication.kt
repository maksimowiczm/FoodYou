package com.maksimowiczm.foodyou

import com.maksimowiczm.foodyou.core.feature.addfood.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.calendar.CalendarFeature
import com.maksimowiczm.foodyou.core.feature.camera.CameraFeature
import com.maksimowiczm.foodyou.core.feature.diary.DiaryFeature
import com.maksimowiczm.foodyou.core.feature.product.ProductFeature
import com.maksimowiczm.foodyou.core.feature.system.SystemFeature
import com.maksimowiczm.foodyou.core.infrastructure.android.FoodYouApplication
import com.maksimowiczm.foodyou.core.feature.FeatureManager
import com.maksimowiczm.foodyou.core.feature.about.AboutFeature

class MyApplication : FoodYouApplication() {
    override fun setupFeatures() {
        FeatureManager.add(
            SystemFeature,
            CalendarFeature,
            ProductFeature,
            DiaryFeature,
            CameraFeature,
            AddFoodFeature,
            AboutFeature
        )
    }
}