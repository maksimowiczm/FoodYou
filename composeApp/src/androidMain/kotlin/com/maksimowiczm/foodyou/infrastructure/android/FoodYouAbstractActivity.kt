package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidClipboardManager
import com.maksimowiczm.foodyou.shared.ui.utils.AndroidDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.shared.ui.utils.DateFormatterProvider
import kotlinx.coroutines.launch

abstract class FoodYouAbstractActivity : AppCompatActivity() {

    fun setContent(content: @Composable () -> Unit) {
        enableEdgeToEdge()
        with<AppCompatActivity, Unit>(this) {
            setContent {
                ClipboardManagerProvider(
                    clipboardManager = AndroidClipboardManager(this)
                ) {
                    DateFormatterProvider(
                        dateFormatter = AndroidDateFormatter(this)
                    ) {
                        content()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            observeShowContentSecurity()
        }
    }

    private suspend fun observeShowContentSecurity() {
        // TODO
//        HideContent(get())
//            .observe()
//            .filterNotNull()
//            .collectLatest {
//                if (it) {
//                    window.setFlags(FLAG_SECURE, FLAG_SECURE)
//                } else {
//                    window.clearFlags(FLAG_SECURE)
//                }
//            }
    }
}
