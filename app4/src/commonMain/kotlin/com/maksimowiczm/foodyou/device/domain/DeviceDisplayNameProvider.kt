package com.maksimowiczm.foodyou.device.domain

/**
 * Functional interface for providing device display names.
 *
 * The display name is the user-facing identifier for the device, such as "John's Phone" or "My
 * Pixel 7".
 */
fun interface DeviceDisplayNameProvider {
    /**
     * Provides the device display name.
     *
     * @return The device display name from platform-specific sources
     */
    suspend fun provide(): String
}
