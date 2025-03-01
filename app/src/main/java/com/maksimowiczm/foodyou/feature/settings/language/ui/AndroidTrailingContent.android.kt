package com.maksimowiczm.foodyou.feature.settings.language.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun AndroidTrailingContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }
    } else {
        Intent()
    }

    val isSystemLocaleSettingsAvailable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_ALL)
                .isNotEmpty()
        } else {
            false
        }

    if (!isSystemLocaleSettingsAvailable) {
        return
    }

    ListItem(
        headlineContent = {
            Text(stringResource(R.string.headline_system_language_settings))
        },
        modifier = modifier.clickable { context.startActivity(intent) },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    )
}

@Preview
@Composable
private fun AndroidTrailingContentPreview() {
    FoodYouTheme {
        AndroidTrailingContent()
    }
}
