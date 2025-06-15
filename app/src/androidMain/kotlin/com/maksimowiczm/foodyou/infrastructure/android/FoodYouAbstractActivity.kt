package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ui.utils.AndroidClipboardManager
import com.maksimowiczm.foodyou.core.ui.utils.AndroidDateFormatter
import com.maksimowiczm.foodyou.core.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.core.ui.utils.DateFormatterProvider
import com.maksimowiczm.foodyou.data.AppPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

abstract class FoodYouAbstractActivity : AppCompatActivity() {

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()
        with<AppCompatActivity, Unit>(this) {
            setContent {
                ClipboardManagerProvider(
                    clipboardManager = AndroidClipboardManager(this)
                ) {
                    DateFormatterProvider(
                        dateFormatter = AndroidDateFormatter(this)
                    ) {
                        content()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            observeShowContentSecurity()
        }
    }

    private suspend fun observeShowContentSecurity() {
        val dataStore = get<DataStore<Preferences>>()

        dataStore
            .observe(AppPreferences.hideContent)
            .filterNotNull()
            .collectLatest {
                if (it) {
                    window.setFlags(FLAG_SECURE, FLAG_SECURE)
                } else {
                    window.clearFlags(FLAG_SECURE)
                }
            }
    }
}
