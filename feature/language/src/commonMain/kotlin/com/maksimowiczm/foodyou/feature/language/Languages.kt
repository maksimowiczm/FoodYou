package com.maksimowiczm.foodyou.feature.language

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

private val me = Author(
    name = "Mateusz Maksimowicz",
    link = "https://github.com/maksimowiczm"
)

val languages = mapOf(
    // If you'd like to be credited for your translations, please add your name here.
    // "language name (Country)" to Translation(
    //     tag = "language-tag",
    //     authorsStrings = listOf(
    //         Author(
    //             name = "Your Name",
    //             // Optional link to your website or profile
    //             link = "https://example.com"
    //         )
    //     )
    // ),
    "English (United States)" to Translation(
        tag = "en-US",
        authorsStrings = listOf(me)
    ),
    "Català (Espanya)" to Translation(
        tag = "ca-ES",
        authorsStrings = emptyList()
    ),
    "Dansk (Danmark)" to Translation(
        tag = "da-DK",
        authorsStrings = emptyList()
    ),
    "Deutsch (Deutschland)" to Translation(
        tag = "de-DE",
        authorsStrings = emptyList()
    ),
    "Español (España)" to Translation(
        tag = "es-ES",
        authorsStrings = emptyList()
    ),
    "Français (France)" to Translation(
        tag = "fr-FR",
        authorsStrings = emptyList()
    ),
    "Italiano (Italia)" to Translation(
        tag = "it-IT",
        authorsStrings = emptyList()
    ),
    "Magyar (Magyarország)" to Translation(
        tag = "hu-HU",
        authorsStrings = emptyList()
    ),
    "Nederlands (Nederland)" to Translation(
        tag = "nl-NL",
        authorsStrings = listOf(
            Author(
                name = "GrizzleNL",
                link = "https://grizzle.nl"
            )
        )
    ),
    "Polski (Polska)" to Translation(
        tag = "pl-PL",
        authorsStrings = listOf(me)
    ),
    "Português (Brasil)" to Translation(
        tag = "pt-BR",
        authorsStrings = emptyList()
    ),
    "Türkçe (Türkiye)" to Translation(
        tag = "tr-TR",
        authorsStrings = listOf(
            Author(
                name = "mikropsoft",
                link = "https://github.com/mikropsoft"
            )
        )
    ),
    "Русский (Россия)" to Translation(
        tag = "ru-RU",
        authorsStrings = listOf()
    ),
    "Українська (Україна)" to Translation(
        tag = "uk-UA",
        authorsStrings = emptyList()
    ),
    "العربية (المملكة العربية السعودية)" to Translation(
        tag = "ar-SA",
        authorsStrings = listOf()
    ),
    "简体中文" to Translation(
        tag = "zh-CN",
        authorsStrings = listOf()
    )
)

data class Translation(val tag: String, val authorsStrings: List<Author>)

data class Author(val name: String, val link: String? = null) {
    @Composable
    fun toAnnotatedString(): AnnotatedString = if (link != null) {
        val linkStyle = MaterialTheme.colorScheme.primary
        val textStyle = LocalTextStyle.current.copy()
        val spanStyle = textStyle.merge(linkStyle).toSpanStyle()

        remember {
            buildAnnotatedString {
                withStyle(
                    style = spanStyle.copy(
                        fontStyle = FontStyle.Italic
                    )
                ) {
                    withLink(
                        LinkAnnotation.Url(
                            url = link
                        )
                    ) {
                        append(name)
                    }
                }
            }
        }
    } else {
        AnnotatedString(name)
    }
}
