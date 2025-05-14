package com.maksimowiczm.foodyou.infrastructure.android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductApp

class ShareProductActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = try {
            extractIntent(intent)
        } catch (_: NoTextFoundException) {
            Logger.e { "No text found in intent" }
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            ShareProductApp(
                text = text,
                onBack = { finish() },
                onCreate = {
                    Toast.makeText(this, "Product created", Toast.LENGTH_LONG).show()
                    finish()
                }
            )
        }
    }

    private fun extractIntent(intent: Intent): String {
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text == null) {
            throw NoTextFoundException()
        }

        return text
    }
}

private class NoTextFoundException : Exception()

@Composable
private fun ShareProductApp(text: String, onBack: () -> Unit, onCreate: () -> Unit) {
    FoodYouTheme {
        CreateProductApp(
            onBack = onBack,
            onCreate = { onCreate() },
            text = text
        )
    }
}
