package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import kotlin.getValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

abstract class FoodYouAbstractActivity : AppCompatActivity() {
    private val systemDetails: SystemDetails by inject()
    private val deviceRepository: DeviceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(systemDetails)

        lifecycleScope.launch { observeShowContentSecurity() }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(systemDetails)

        super.onDestroy()
    }

    private suspend fun observeShowContentSecurity() {
        deviceRepository
            .observe()
            .map { it.hideScreen }
            .collectLatest {
                if (it) {
                    window.setFlags(FLAG_SECURE, FLAG_SECURE)
                } else {
                    window.clearFlags(FLAG_SECURE)
                }
            }
    }
}
