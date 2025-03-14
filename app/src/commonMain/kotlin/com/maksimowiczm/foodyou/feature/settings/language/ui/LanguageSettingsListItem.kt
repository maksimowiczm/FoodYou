package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

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
            Text(stringResource(R.string.headline_language))
        },
        modifier = modifier.clickable { onClick() },
        supportingContent = {
            Text(language)
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_language_24),
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
