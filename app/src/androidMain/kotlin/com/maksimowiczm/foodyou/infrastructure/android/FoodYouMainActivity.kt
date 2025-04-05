package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.maksimowiczm.foodyou.ui.FoodYouApp

class FoodYouMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            FoodYouApp()
        }
    }
}
