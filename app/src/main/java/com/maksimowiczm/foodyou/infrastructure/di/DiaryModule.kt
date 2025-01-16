package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.diary.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.feature.diary.data.AndroidTodayDateProvider
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.data.SystemInfoRepository
import com.maksimowiczm.foodyou.feature.diary.data.TodayDateProvider
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryModule = module {
    viewModelOf(::DiaryViewModel)

    factory {
        AndroidSystemInfoRepository(
            context = get()
        )
    }.bind<SystemInfoRepository>()

    factory {
        DiaryRepositoryImpl(
            dataStore = get(),
            diaryDao = get()
        )
    }.bind<DiaryRepository>()

    single {
        AndroidTodayDateProvider()
    }.bind<TodayDateProvider>()
}
