package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder

val LocalNutrientsOrder = staticCompositionLocalOf { NutrientsOrder.defaultOrder }
