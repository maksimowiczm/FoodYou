package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.ui.FoodYouApp
import org.koin.android.ext.android.inject

class FoodYouMainActivity : AppCompatActivity() {
    val featureManager: FeatureManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeFeatures = featureManager.get<Feature.Home>()
        val settingsFeatures = featureManager.get<Feature.Settings>()

        enableEdgeToEdge()
        setContent {
            FoodYouApp(
                homeFeatures = homeFeatures,
                settingsFeatures = settingsFeatures
            )
        }
    }
}
