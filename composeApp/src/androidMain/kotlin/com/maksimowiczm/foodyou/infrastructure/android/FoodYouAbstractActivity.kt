package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveSettingsQuery
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.system.AndroidSystemDetails
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidClipboardManager
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.shared.ui.utils.DateFormatterProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

abstract class FoodYouAbstractActivity : AppCompatActivity() {

    private val systemDetails: AndroidSystemDetails
        get() = get()

    private val queryBus: QueryBus
        get() = get()

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()
        with<AppCompatActivity, Unit>(this) {
            setContent {
                ClipboardManagerProvider(AndroidClipboardManager(this)) {
                    DateFormatterProvider(AndroidDateFormatter(this)) { content() }
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
        queryBus
            .dispatch(ObserveSettingsQuery)
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
