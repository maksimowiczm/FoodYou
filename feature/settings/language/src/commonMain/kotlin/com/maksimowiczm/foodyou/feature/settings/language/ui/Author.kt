package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.maksimowiczm.foodyou.app.business.opensource.domain.translation.Author

@Composable
internal fun Author.toAnnotatedString(): AnnotatedString =
    link?.let { link ->
        val linkStyle = MaterialTheme.colorScheme.primary
        val textStyle = LocalTextStyle.current.copy()
        val spanStyle = textStyle.merge(linkStyle).toSpanStyle()

        remember(linkStyle, textStyle, spanStyle, this) {
            buildAnnotatedString {
                withStyle(style = spanStyle.copy(fontStyle = FontStyle.Italic)) {
                    withLink(LinkAnnotation.Url(url = link)) { append(name) }
                }
            }
        }
    } ?: remember(this) { AnnotatedString(name) }
