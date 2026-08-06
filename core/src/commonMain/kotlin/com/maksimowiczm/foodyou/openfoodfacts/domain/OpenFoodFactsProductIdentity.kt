package com.maksimowiczm.foodyou.openfoodfacts.domain

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Open Food Facts identifier using product barcode.
 *
 * @property barcode The product barcode
 */
@Serializable @JvmInline value class OpenFoodFactsProductIdentity(val barcode: String)
