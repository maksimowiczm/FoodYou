package com.maksimowiczm.foodyou.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.common.component.InteractiveLogo

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        InteractiveLogo(
            Modifier.align(Alignment.Center).safeContentPadding().fillMaxWidth().aspectRatio(1f)
        )
    }
}
