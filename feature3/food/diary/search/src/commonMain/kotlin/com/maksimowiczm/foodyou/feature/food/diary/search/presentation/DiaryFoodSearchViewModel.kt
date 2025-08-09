package com.maksimowiczm.foodyou.feature.food.diary.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class DiaryFoodSearchViewModel(mealId: Long, private val queryBus: QueryBus) :
    ViewModel() {
    val meal =
        queryBus
            .dispatch<Meal?>(ObserveMealQuery(mealId))
            .mapIfNotNull { MealModel(id = it.id, name = it.name) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
