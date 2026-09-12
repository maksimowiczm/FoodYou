package com.maksimowiczm.foodyou

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import com.maksimowiczm.foodyou.shared.ui.utility.DateFormatterImpl
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.UIFeatureFlags
import com.maksimowiczm.foodyou.shared.ui.utility.UtilityProvider
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

abstract class FoodYouAbstractActivity : AppCompatActivity() {
    private val systemDetails: SystemDetails by inject()
    private val deviceSettingsRepository: DeviceSettingsRepository by inject()
    private val foodNameSelector: FoodNameSelector by inject()
    private val appConfig: FoodYouConfig by inject()
    private val blobResolver: BlobResolver by inject()
    private val accountService: AccountService by inject()
    private val featureFlags: StateFlow<UIFeatureFlags> by
        lazy(LazyThreadSafetyMode.NONE) {
            accountService
                .observe()
                .filterNotNull()
                .map { UIFeatureFlags(singleProfileMode = it.singleProfileMode) }
                .stateIn(
                    scope = lifecycleScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = UIFeatureFlags(),
                )
        }

    fun setContent(content: @Composable () -> Unit) {
        val dateFormatter = DateFormatterImpl(this)
        with(this as AppCompatActivity) {
            setContent {
                val uiFeatureFlags by featureFlags.collectAsStateWithLifecycle()
                UtilityProvider(
                    dateFormatter = dateFormatter,
                    foodNameSelector = foodNameSelector,
                    appConfig = appConfig,
                    blobResolver = blobResolver,
                    uiFeatureFlags = uiFeatureFlags,
                    content = content,
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(systemDetails)
        lifecycleScope.launch { observeShowContentSecurity() }
        FileKit.init(this)
        enableEdgeToEdge()
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
