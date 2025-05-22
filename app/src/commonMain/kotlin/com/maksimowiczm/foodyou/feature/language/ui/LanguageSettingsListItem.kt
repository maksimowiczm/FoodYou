package com.maksimowiczm.foodyou.feature.language.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LanguageSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    viewModel: LanguageViewModel = koinViewModel()
) {
    val language = remember { viewModel.languageName }

    LanguageSettingsListItem(
        onClick = onClick,
        language = language,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
private fun LanguageSettingsListItem(
    onClick: () -> Unit,
    language: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_language))
        },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Text(language)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
