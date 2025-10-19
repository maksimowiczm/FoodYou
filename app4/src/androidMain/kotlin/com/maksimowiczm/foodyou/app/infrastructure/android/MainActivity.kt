package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newText = intent.getStringExtra(Intent.EXTRA_TEXT)

        enableEdgeToEdge()
        setContent { FoodYouApp(newText) }
    }
}
