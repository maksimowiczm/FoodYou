package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder

val LocalNutrientsOrder = staticCompositionLocalOf { NutrientsOrder.defaultOrder }
