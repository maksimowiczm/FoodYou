package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.productredesign.ui.CreateProductApp

@Composable
fun CreateProductScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    CreateProductApp(
        onBack = onBack,
        modifier = modifier
    )
}
