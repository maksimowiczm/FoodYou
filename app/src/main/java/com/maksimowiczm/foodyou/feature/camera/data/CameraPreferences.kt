package com.maksimowiczm.foodyou.feature.camera.data

import androidx.datastore.preferences.core.intPreferencesKey

object CameraPreferences {
    val cameraPermissionRequests = intPreferencesKey("camera_permission_requests")
}
