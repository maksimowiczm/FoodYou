package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FetchProgressIndicator(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Spacer(Modifier.height(8.dp))
        if (isLoading) LinearWavyProgressIndicator(Modifier.fillMaxWidth())
        else Spacer(Modifier.height(10.dp))
        Spacer(Modifier.height(8.dp))
    }
}
