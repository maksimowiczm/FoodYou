package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.feature.security.data.SecurityPreferences
import com.maksimowiczm.foodyou.ui.FoodYouApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class FoodYouMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            observeShowContentSecurity()
        }

        enableEdgeToEdge()
        setContent {
            ClipboardManagerProvider(
                clipboardManager = AndroidClipboardManager(this)
            ) {
                FoodYouApp()
            }
        }
    }

    private suspend fun observeShowContentSecurity() {
        val dataStore = get<DataStore<Preferences>>()

        dataStore
            .observe(SecurityPreferences.hideContent)
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
