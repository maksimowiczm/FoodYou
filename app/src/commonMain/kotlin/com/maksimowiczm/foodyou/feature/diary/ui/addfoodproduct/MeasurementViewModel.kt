package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
import kotlinx.coroutines.flow.Flow

abstract class MeasurementViewModel : ViewModel() {
    abstract val product: Flow<Product?>
    abstract fun onDelete()
}
