package com.maksimowiczm.foodyou.fooddatacentral.domain

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * FoodData Central identifier from the USDA database.
 *
 * @property fdcId The FoodData Central unique identifier
 */
@Serializable @JvmInline value class FoodDataCentralProductIdentity(val fdcId: Int)
