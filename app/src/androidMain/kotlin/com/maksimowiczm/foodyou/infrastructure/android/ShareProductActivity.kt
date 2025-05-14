package com.maksimowiczm.foodyou.infrastructure.android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

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
        ShareProductNavHost(
            text = text,
            onBack = onBack,
            onCreate = onCreate
        )
    }
}

@Composable
private fun ShareProductNavHost(
    text: String,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "share_product",
        modifier = modifier
    ) {
        composable("share_product") {
            Text(
                text = text,
                modifier = Modifier.safeContentPadding()
            )
        }
    }
}
