package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FoodYouApp() }
    }
}
