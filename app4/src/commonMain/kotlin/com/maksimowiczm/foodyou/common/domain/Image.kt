package com.maksimowiczm.foodyou.common.domain

/**
 * Represents an image that can be displayed in the application.
 *
 * This sealed interface provides a type-safe way to handle images from different sources, ensuring
 * proper handling based on whether the image is stored remotely or locally on the device.
 */
sealed interface Image {
    /**
     * Represents an image hosted on a remote server.
     *
     * @property url The HTTP/HTTPS URL pointing to the remote image resource.
     */
    data class Remote(val url: String) : Image

    /**
     * Represents an image stored locally on the device.
     *
     * @property uri The local URI pointing to the image file on the device's file system.
     */
    data class Local(val uri: String) : Image
}
