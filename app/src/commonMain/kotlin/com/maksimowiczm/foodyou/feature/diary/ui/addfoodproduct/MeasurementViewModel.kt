package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Food
import kotlinx.coroutines.flow.Flow

abstract class MeasurementViewModel : ViewModel() {
    abstract val food: Flow<Food?>
    abstract fun onDelete()
}
