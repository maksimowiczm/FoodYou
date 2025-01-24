package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import com.maksimowiczm.foodyou.ui.FoodYouApp
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val dataStore: DataStore<Preferences> by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // TODO settings
            dataStore.set(
                ProductPreferences.openFoodFactsEnabled to true,
                ProductPreferences.openFoodCountryCode to "pl"
            )
        }

        enableEdgeToEdge()
        setContent {
            FoodYouApp()
        }
    }
}
