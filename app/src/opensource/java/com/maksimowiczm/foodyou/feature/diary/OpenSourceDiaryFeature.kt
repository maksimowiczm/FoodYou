package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsFeature
import org.koin.core.module.dsl.factoryOf

object OpenSourceDiaryFeature : Feature.Koin, DiaryFeature(
    diaryRepository = {
        factoryOf(::DiaryRepositoryImpl)
    },
    productFeature = OpenFoodFactsFeature
)
