package com.maksimowiczm.foodyou

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {
    private var sharedText = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedText.value = intent.getStringExtra(Intent.EXTRA_TEXT)

        enableEdgeToEdge()
        setContent { FoodYouApp(sharedText.value) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedText.value = intent.getStringExtra(Intent.EXTRA_TEXT)
    }
}
