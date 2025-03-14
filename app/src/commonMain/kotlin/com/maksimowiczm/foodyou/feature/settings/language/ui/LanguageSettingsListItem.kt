package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = koinViewModel()
) {
    val language = remember { viewModel.languageName }

    LanguageSettingsListItem(
        onClick = onClick,
        language = language,
        modifier = modifier
    )
}

@Composable
private fun LanguageSettingsListItem(
    onClick: () -> Unit,
    language: String,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_language))
        },
        modifier = modifier.clickable { onClick() },
        supportingContent = {
            Text(language)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null
            )
        }
    )
}

@Preview
@Composable
private fun StyleSettingsListItemPreview() {
    FoodYouTheme {
        LanguageSettingsListItem(
            onClick = {},
            language = "English"
        )
    }
}
