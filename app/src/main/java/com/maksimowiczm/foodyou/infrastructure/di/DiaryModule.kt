package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryModule = module {
    viewModelOf(::DiaryViewModel)
}
