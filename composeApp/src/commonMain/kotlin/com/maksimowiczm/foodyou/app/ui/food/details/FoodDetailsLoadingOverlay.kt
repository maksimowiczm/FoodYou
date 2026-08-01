package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun FoodDetailsLoadingOverlay(isLoading: Boolean, modifier: Modifier = Modifier) {
    Column(modifier) {
        if (isLoading) {
            Spacer(Modifier.height(8.dp))
            LinearWavyProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        } else {
            Spacer(Modifier.height(26.dp))
        }
    }
}
