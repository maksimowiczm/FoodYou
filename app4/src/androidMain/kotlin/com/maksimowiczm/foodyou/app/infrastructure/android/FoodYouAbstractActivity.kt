package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.app.ui.common.utility.AndroidClipboardManager
import com.maksimowiczm.foodyou.app.ui.common.utility.AndroidDateFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.AppConfigProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.ClipboardManagerProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.ClockProvider
import com.maksimowiczm.foodyou.app.ui.common.utility.DateFormatterProvider
import com.maksimowiczm.foodyou.app.ui.food.FoodNameSelectorProvider
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.common.infrastructure.defaultLocale
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import foodyou.app.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlin.time.Clock
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

abstract class FoodYouAbstractActivity : AppCompatActivity() {
    private val systemDetails: SystemDetails by inject()
    private val deviceRepository: DeviceRepository by inject()
    private val foodNameSelector: FoodNameSelector by inject()
    private val clock: Clock by inject()
    private val appConfig: AppConfig by inject()

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()

        FileKit.init(this)

        val clipboardManager =
            AndroidClipboardManager(this) {
                runBlocking { org.jetbrains.compose.resources.getString(Res.string.neutral_copied) }
            }
        val dateFormatter = AndroidDateFormatter(this) { defaultLocale }

        with<AppCompatActivity, Unit>(this) {
            setContent {
                ClipboardManagerProvider(clipboardManager) {
                    DateFormatterProvider(dateFormatter) {
                        ClockProvider(clock) {
                            FoodNameSelectorProvider(foodNameSelector) {
                                AppConfigProvider(appConfig) { content() }
                            }
                        }
                    }
                }
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
