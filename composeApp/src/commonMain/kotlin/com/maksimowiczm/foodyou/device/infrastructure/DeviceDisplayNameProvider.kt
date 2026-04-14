package com.maksimowiczm.foodyou.device.infrastructure

import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

/**
 * Functional interface for providing device display names.
 *
 * The display name is the user-facing identifier for the device, such as "John's Phone" or "My
 * Pixel 7".
 */
internal expect class DeviceDisplayNameProvider {
    /**
     * Provides the device display name.
     *
     * @return The device display name from platform-specific sources
     */
    suspend fun provide(): String
}

internal expect fun Module.deviceDisplayNameProvider():
    KoinDefinition<out DeviceDisplayNameProvider>
