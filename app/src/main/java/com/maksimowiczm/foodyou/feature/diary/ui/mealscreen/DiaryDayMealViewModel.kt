package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.DiaryFeature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import kotlinx.datetime.LocalDate

class DiaryDayMealViewModel(diaryRepository: DiaryRepository, savedStateHandle: SavedStateHandle) :
    DiaryViewModel(diaryRepository) {
    val mealId = savedStateHandle.toRoute<DiaryFeature.Meal>().mealId
    val date = LocalDate.fromEpochDays(savedStateHandle.toRoute<DiaryFeature.Meal>().epochDay)
}
