package com.maksimowiczm.foodyou.infrastructure.android

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.ui.utils.AndroidClipboardManager
import com.maksimowiczm.foodyou.core.ui.utils.AndroidDateFormatter
import com.maksimowiczm.foodyou.core.ui.utils.ClipboardManagerProvider
import com.maksimowiczm.foodyou.core.ui.utils.DateFormatterProvider

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
}
