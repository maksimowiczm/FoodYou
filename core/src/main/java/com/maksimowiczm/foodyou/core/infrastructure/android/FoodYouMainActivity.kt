package com.maksimowiczm.foodyou.core.infrastructure.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.FeatureManager
import com.maksimowiczm.foodyou.core.ui.FoodYouApp

open class FoodYouMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeFeatures = FeatureManager.get<Feature.Home>()
        val settingsFeatures = FeatureManager.get<Feature.Settings>()

        enableEdgeToEdge()
        setContent {
            FoodYouApp(
                homeFeatures = homeFeatures,
                settingsFeatures = settingsFeatures
            )
        }
    }
}
