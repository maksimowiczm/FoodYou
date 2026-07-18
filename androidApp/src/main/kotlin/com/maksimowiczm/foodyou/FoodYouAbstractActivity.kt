package com.maksimowiczm.foodyou

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.app.ui.common.utility.ClipboardManagerImpl
import com.maksimowiczm.foodyou.app.ui.common.utility.DateFormatterImpl
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.UIFeatureFlags
import com.maksimowiczm.foodyou.app.ui.common.utility.UtilityProvider
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

abstract class FoodYouAbstractActivity : AppCompatActivity() {
    private val systemDetails: SystemDetails by inject()
    private val deviceSettingsRepository: DeviceSettingsRepository by inject()
    private val foodNameSelector: FoodNameSelector by inject()
    private val appConfig: FoodYouConfig by inject()
    private val blobResolver: BlobResolver by inject()

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()

        FileKit.init(this)

        val clipboardManager = ClipboardManagerImpl(this)
        val dateFormatter = DateFormatterImpl(this)

        with<AppCompatActivity, Unit>(this) {
            setContent {
                UtilityProvider(
                    clipboardManager = clipboardManager,
                    dateFormatter = dateFormatter,
                    foodNameSelector = foodNameSelector,
                    appConfig = appConfig,
                    blobResolver = blobResolver,
                    uiFeatureFlags = UIFeatureFlags,
                    content = content,
                )
            }
        }
    }

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
        deviceSettingsRepository
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
