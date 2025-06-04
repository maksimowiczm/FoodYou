package com.maksimowiczm.foodyou.infrastructure.android

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.product.CreateProductFromUrlScreen
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
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
                onCreate = { onCreated() }
            )
        }
    }

    private fun handleIntent(intent: Intent) {
        val newText = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (newText != null) {
            sharedText.value = newText
        } else {
            Logger.e("No text found in intent")
            val error = runBlocking { getStringRes(Res.string.error_unknown_error) }
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
private fun ShareProductApp(text: String?, onBack: () -> Unit, onCreate: () -> Unit) {
    FoodYouTheme {
        Surface {
            if (text == null) {
                Spacer(Modifier.fillMaxSize())
            } else {
                CreateProductFromUrlScreen(
                    url = text,
                    onBack = onBack,
                    onCreate = { onCreate() }
                )
            }
        }
    }
}
