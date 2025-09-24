package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.settings.domain.entity.NutrientsOrder

val LocalNutrientsOrder = staticCompositionLocalOf { NutrientsOrder.defaultOrder }

@Composable
fun NutrientsOrderProvider(order: List<NutrientsOrder>, content: @Composable () -> Unit) {

    CompositionLocalProvider(LocalNutrientsOrder provides order) { content() }
}
