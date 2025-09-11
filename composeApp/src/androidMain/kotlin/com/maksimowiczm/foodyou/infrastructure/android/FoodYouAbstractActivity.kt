package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.Settings
import com.maksimowiczm.foodyou.app.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.shared.common.infrastructure.system.defaultLocale
import com.maksimowiczm.foodyou.shared.compose.utility.AndroidClipboardManager
import com.maksimowiczm.foodyou.shared.compose.utility.AndroidDateFormatter
import com.maksimowiczm.foodyou.shared.compose.utility.ClipboardManagerProvider
import com.maksimowiczm.foodyou.shared.compose.utility.DateFormatterProvider
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.neutral_copied
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

abstract class FoodYouAbstractActivity : AppCompatActivity() {

    private val systemDetails: SystemDetails
        get() = get()

    private val settingsRepository: UserPreferencesRepository<Settings>
        get() = get(named(Settings::class.qualifiedName!!))

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()

        val clipboardManager =
            AndroidClipboardManager(this) { runBlocking { getString(Res.string.neutral_copied) } }
        val dateFormatter = AndroidDateFormatter(this) { this.defaultLocale }

        with<AppCompatActivity, Unit>(this) {
            setContent {
                ClipboardManagerProvider(clipboardManager) {
                    DateFormatterProvider(dateFormatter) { content() }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch { observeShowContentSecurity() }

        lifecycle.addObserver(systemDetails)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(systemDetails)
    }

    private suspend fun observeShowContentSecurity() {
        settingsRepository
            .observe()
            .map { it.secureScreen }
            .collectLatest {
                if (it) {
                    window.setFlags(FLAG_SECURE, FLAG_SECURE)
                } else {
                    window.clearFlags(FLAG_SECURE)
                }
            }
    }
}
