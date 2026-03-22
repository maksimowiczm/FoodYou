package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun FoodDetailsLoadingOverlay(isLoading: Boolean, topPadding: Dp) {
    Column(Modifier.padding(top = topPadding).zIndex(100f)) {
        if (isLoading) {
            Spacer(Modifier.height(8.dp))
            LinearWavyProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        } else {
            Spacer(Modifier.height(26.dp))
        }
    }
}
