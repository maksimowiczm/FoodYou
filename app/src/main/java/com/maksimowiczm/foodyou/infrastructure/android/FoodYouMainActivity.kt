package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.data.SecurityPreferences
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.ui.FoodYouApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FoodYouMainActivity : AppCompatActivity() {
    val featureManager: FeatureManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            observeShowContentSecurity()
        }

        enableEdgeToEdge()
        setContent {
            FoodYouApp(
                homeFeatures = featureManager.get(),
                settingsFeatures = featureManager.get()
            )
        }
    }

    private suspend fun observeShowContentSecurity() {
        val dataStore = get<DataStore<Preferences>>()

        dataStore.observe(SecurityPreferences.showContent).collectLatest {
            if (it == true) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                window.clearFlags(
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            }
        }
    }
}
