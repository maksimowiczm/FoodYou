package com.maksimowiczm.foodyou.openfoodfacts.domain

import kotlin.jvm.JvmInline

/**
 * Open Food Facts identifier using product barcode.
 *
 * @property barcode The product barcode
 */
@JvmInline value class OpenFoodFactsProductIdentity(val barcode: String)
