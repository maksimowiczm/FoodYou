package com.maksimowiczm.foodyou.feature.settings.master.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.shared.domain.translation.TranslationRepository
import com.maksimowiczm.foodyou.app.ui.shared.component.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun LanguageSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val translationRepository: TranslationRepository = koinInject()
    val language = translationRepository.observeCurrent().collectAsStateWithLifecycle(null).value

    SettingsListItem(
        icon = { Icon(Icons.Outlined.Translate, null) },
        label = { Text(stringResource(Res.string.headline_language)) },
        supportingContent = { language?.languageName?.let { Text(it) } },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
    )
}
