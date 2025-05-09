package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Activity
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.maksimowiczm.foodyou.core.domain.usecase.ExtractProductLinkFromTextUseCase
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString as getStringRes
import org.koin.android.ext.android.inject

class ShareProductActivity : Activity() {

    private val extractLinkUseCase: ExtractProductLinkFromTextUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = runCatching { extractIntent(intent) }.getOrElse {
            when (it) {
                is NoProductLinkFoundException -> {
                    val text = runBlocking { getStringRes(Res.string.error_url_is_not_supported) }
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    Log.w("ShareProductActivity", "No product link found")
                }

                else -> {
                    val text = runBlocking { getStringRes(Res.string.error_something_went_wrong) }
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    Log.e("ShareProductActivity", "Error extracting intent", it)
                }
            }

            finish()
            return
        }

        if (intent == null) {
            Log.e("ShareProductActivity", "Intent is null")
            finish()
            return
        }

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        try {
            pendingIntent?.send()
        } catch (e: PendingIntent.CanceledException) {
            Log.e("ShareProductActivity", "PendingIntent was canceled", e)
        } catch (e: Exception) {
            Log.e("ShareProductActivity", "Error sending PendingIntent", e)
        } finally {
            finish()
        }
    }

    private fun extractIntent(intent: Intent?): Intent? {
        if (intent == null) {
            return null
        }

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text == null) {
            throw NoTextFoundException()
        }

        val url = extractLinkUseCase(text)

        if (url == null) {
            throw NoProductLinkFoundException()
        }

        return Intent(
            Intent.ACTION_VIEW,
            "foodyou://createproduct?url=$url".toUri(),
            this@ShareProductActivity,
            FoodYouMainActivity::class.java
        )
    }
}

private class NoTextFoundException : Exception()

private class NoProductLinkFoundException : Exception()
