package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {
    private var sharedText: String? = null
    private val sharedClock = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)

        enableEdgeToEdge()
        setContent { key(sharedClock.intValue) { FoodYouApp(sharedText) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        sharedClock.intValue = (sharedClock.intValue + 1) % Int.MAX_VALUE
    }
}
