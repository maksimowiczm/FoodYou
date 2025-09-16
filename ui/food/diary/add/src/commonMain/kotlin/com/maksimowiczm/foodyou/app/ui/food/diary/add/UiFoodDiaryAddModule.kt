package com.maksimowiczm.foodyou.app.ui.food.diary.add

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodDiaryAddModule = module { viewModelOf(::AddEntryViewModel) }
