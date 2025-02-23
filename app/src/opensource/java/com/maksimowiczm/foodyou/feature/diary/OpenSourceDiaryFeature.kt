package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.addfood.OpenSourceAddFoodFeature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepositoryImpl
import org.koin.core.module.dsl.factoryOf

object OpenSourceDiaryFeature : Feature.Koin, DiaryFeature(
    diaryRepository = {
        factoryOf(::DiaryRepositoryImpl)
    },
    addFoodFeature = OpenSourceAddFoodFeature
)
