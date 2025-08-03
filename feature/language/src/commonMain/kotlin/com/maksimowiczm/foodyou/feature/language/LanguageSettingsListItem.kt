package com.maksimowiczm.foodyou.feature.language

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.core.ui.SettingsListItem
import com.maksimowiczm.foodyou.feature.language.ui.LanguageViewModel
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LanguageSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<LanguageViewModel>()
    val language = remember(viewModel) { viewModel.languageName }

    SettingsListItem(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Translate,
                contentDescription = null
            )
        },
        label = { Text(stringResource(Res.string.headline_language)) },
        supportingContent = { Text(language) },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor
    )
}
