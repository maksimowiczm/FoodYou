package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryModule = module {
    viewModelOf(::DiaryViewModel)

    factory {
        DiaryRepositoryImpl(
            addFoodDatabase = get(),
            dataStore = get()
        )
    }.bind<DiaryRepository>()
}
