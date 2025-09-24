package com.maksimowiczm.foodyou.app.ui.food.diary

import com.maksimowiczm.foodyou.app.ui.food.diary.add.foodDiaryAdd
import com.maksimowiczm.foodyou.app.ui.food.diary.quickadd.foodDiaryQuickAdd
import com.maksimowiczm.foodyou.app.ui.food.diary.search.foodDiarySearch
import com.maksimowiczm.foodyou.app.ui.food.diary.update.foodDiaryUpdate
import org.koin.core.module.Module

fun Module.foodDiary() {
    foodDiaryAdd()
    foodDiaryQuickAdd()
    foodDiarySearch()
    foodDiaryUpdate()
}
