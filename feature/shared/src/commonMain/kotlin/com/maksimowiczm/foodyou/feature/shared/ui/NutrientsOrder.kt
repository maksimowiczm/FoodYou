package com.maksimowiczm.foodyou.feature.shared.ui

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder

val LocalNutrientsOrder = staticCompositionLocalOf { NutrientsOrder.defaultOrder }

@Composable
fun NutrientsOrderProvider(order: List<NutrientsOrder>, content: @Composable () -> Unit) {

    CompositionLocalProvider(LocalNutrientsOrder provides order) { content() }
}
