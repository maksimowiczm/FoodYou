package com.maksimowiczm.foodyou.core.feature.camera.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryPreferences
import com.maksimowiczm.foodyou.core.infrastructure.datastore.observe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CameraBarcodeScannerViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    val cameraPermissionRequests = dataStore
        .observe(key)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    fun onPermissionRequested() {
        viewModelScope.launch {
            dataStore.edit {
                val current = it[key] ?: 0
                it[key] = current + 1
            }
        }
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            dataStore.edit {
                it[key] = 0
            }
        }
    }

    companion object {
        private val key = DiaryPreferences.cameraPermissionRequests
    }
}
