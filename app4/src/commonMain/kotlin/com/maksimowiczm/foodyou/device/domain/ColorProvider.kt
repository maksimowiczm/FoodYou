package com.maksimowiczm.foodyou.device.domain

fun interface ColorProvider {

    /**
     * Generates a random color as an unsigned long integer in ARGB format.
     *
     * @param alpha The alpha component (0-255) to be used in the color.
     * @return A random color represented as a ULong in ARGB format.
     */
    fun random(alpha: Int): ULong
}
