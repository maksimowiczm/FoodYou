package com.maksimowiczm.foodyou.fooddatacentral.domain

import kotlin.jvm.JvmInline

/**
 * FoodData Central identifier from the USDA database.
 *
 * @property fdcId The FoodData Central unique identifier
 */
@JvmInline value class FoodDataCentralProductIdentity(val fdcId: Int)
