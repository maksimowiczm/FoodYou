package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.DownloadProductApp
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString as getStringRes

// Use singleTop and mutable state because otherwise there are weird behaviors with creating
// multiple activities
class ShareProductActivity : FoodYouAbstractActivity() {

    private val sharedText = mutableStateOf<String?>(null)

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        val onCreated = {
            val text = runBlocking { getStringRes(Res.string.neutral_product_created) }
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            finish()
        }

        setContent {
            ShareProductApp(
                text = sharedText.value,
                onBack = { finish() },
                onCreate = { onCreated() },
            )
        }
    }

    private fun handleIntent(intent: Intent) {
        val newText = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (newText != null) {
            sharedText.value = newText
        } else {
            Log.e(TAG, "No text found in intent")
            val error = runBlocking { getStringRes(Res.string.error_unknown_error) }
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val TAG = "ShareProductActivity"
    }
}

@Composable
private fun ShareProductApp(text: String?, onBack: () -> Unit, onCreate: () -> Unit) {
    FoodYouTheme {
        Surface {
            if (text == null) {
                Spacer(Modifier.fillMaxSize())
            } else {
                DownloadProductApp(onBack = onBack, onCreate = onCreate, url = text)
            }
        }
    }
}
