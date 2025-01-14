package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DiaryViewModel : ViewModel() {
    val message: Flow<String> = flow {
        var i = 0

        while (true) {
            emit("Hello, World! ${i++}")
            delay(1000L)
        }
    }
}
