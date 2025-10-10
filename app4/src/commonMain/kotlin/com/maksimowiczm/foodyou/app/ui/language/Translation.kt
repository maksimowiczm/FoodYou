package com.maksimowiczm.foodyou.app.ui.language

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

data class Translation(
    val languageName: String,
    val languageTag: String,
    val authorsStrings: List<Author>,
    val isVerified: Boolean = false,
) {
    constructor(
        languageName: String,
        languageTag: String,
        isVerified: Boolean = false,
        vararg authors: Author,
    ) : this(
        languageName = languageName,
        languageTag = languageTag,
        authorsStrings = authors.toList(),
        isVerified = isVerified,
    )
}

data class Author(val name: String, val link: String? = null)

@Composable
fun Author.toAnnotatedString(): AnnotatedString =
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

// Me
private val me = Author("Mateusz Maksimowicz", "https://github.com/maksimowiczm")

// Someone who helped with the translation
private val grizzleNL = Author("GrizzleNL", "https://grizzle.nl")
private val mikropsoft = Author("mikropsoft", "https://github.com/mikropsoft")

private val EnglishUS =
    Translation(languageName = "English (United States)", languageTag = "en-US", isVerified = true)

val languages =
    listOf(
        // If you'd like to be credited for your translations, please add your name here.
        // "language name (Country)" to Translation(
        //     tag = "language-tag",
        //      listOf(
        //         Author(
        //             name = "Your Name",
        //             // Optional link to your website or profile
        //             link = "https://example.com"
        //         )
        //     )
        // ),
        EnglishUS,
        Translation("Català (Espanya)", "ca-ES"),
        Translation("Dansk (Danmark)", "da-DK"),
        Translation("Deutsch (Deutschland)", "de-DE"),
        Translation("Español (España)", "es-ES"),
        Translation("Français (France)", "fr-FR"),
        Translation("Italiano (Italia)", "it-IT"),
        Translation("Magyar (Magyarország)", "hu-HU"),
        Translation("Nederlands (Nederland)", "nl-NL", false, grizzleNL),
        Translation("Polski (Polska)", "pl-PL", true, me),
        Translation("Português (Brasil)", "pt-BR"),
        Translation("Türkçe (Türkiye)", "tr-TR", false, mikropsoft),
        Translation("Русский (Россия)", "ru-RU"),
        Translation("Українська (Україна)", "uk-UA"),
        Translation("العربية (المملكة العربية السعودية)", "ar-SA"),
        Translation("简体中文", "zh-CN"),
    )
