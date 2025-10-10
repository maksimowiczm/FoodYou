package com.maksimowiczm.foodyou.app.infrastructure.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.maksimowiczm.foodyou.app.ui.FoodYouApp
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val systemDetails: SystemDetails by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(systemDetails)

        enableEdgeToEdge()

        setContent { FoodYouApp() }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(systemDetails)
    }
}
