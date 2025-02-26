package com.maksimowiczm.foodyou.data.preferences

import androidx.datastore.preferences.core.intPreferencesKey

object CameraPreferences {
    val cameraPermissionRequests = intPreferencesKey("camera_permission_requests")
}
