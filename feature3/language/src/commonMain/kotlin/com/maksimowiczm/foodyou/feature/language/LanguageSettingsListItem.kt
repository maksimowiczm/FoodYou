package com.maksimowiczm.foodyou.feature.language

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.feature.language.ui.LanguageViewModel
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_language
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val viewModel = koinViewModel<LanguageViewModel>()
    val language = remember(viewModel) { viewModel.languageName }

    ListItem(
        headlineContent = { Text(stringResource(Res.string.headline_language)) },
        modifier = modifier.clickable { onClick() },
        supportingContent = { Text(language) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Translate,
                contentDescription = null
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            leadingIconColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}
