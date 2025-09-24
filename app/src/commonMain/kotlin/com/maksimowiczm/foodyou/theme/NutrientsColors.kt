package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences

/**
 * Colors for nutrients used in the app theme.
 *
 * @param proteins Color for proteins in ARGB format (e.g., 0xFFFF0000 for red). Null if not set.
 * @param carbohydrates Color for carbohydrates in ARGB format. Null if not set.
 * @param fats Color for fats in ARGB format. Null if not set.
 */
data class NutrientsColors(val proteins: ULong?, val carbohydrates: ULong?, val fats: ULong?) :
    UserPreferences
