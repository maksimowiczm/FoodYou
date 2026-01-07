package com.maksimowiczm.foodyou.app.ui.language

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.maksimowiczm.foodyou.common.domain.Language

data class Translation(
    val languageName: String,
    val language: Language,
    val authorsStrings: List<Author>,
    val isVerified: Boolean = false,
) {
    constructor(
        languageName: String,
        language: Language,
        isVerified: Boolean = false,
        vararg authors: Author,
    ) : this(
        languageName = languageName,
        language = language,
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
    Translation(
        languageName = "English (United States)",
        language = Language.English,
        isVerified = true,
    )

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
        Translation("Català (Espanya)", Language.Catalan),
        Translation("Dansk (Danmark)", Language.Danish),
        Translation("Deutsch (Deutschland)", Language.German),
        Translation("Español (España)", Language.Spanish),
        Translation("Français (France)", Language.French),
        Translation("Indonesian (Indonesia)", Language.Indonesian),
        Translation("Italiano (Italia)", Language.Italian),
        Translation("Magyar (Magyarország)", Language.Hungarian),
        Translation("Nederlands (Nederland)", Language.Dutch, false, grizzleNL),
        Translation("Polski (Polska)", Language.Polish, true, me),
        Translation("Português (Brasil)", Language.PortugueseBrazil),
        Translation("Türkçe (Türkiye)", Language.Turkish, false, mikropsoft),
        Translation("Русский (Россия)", Language.Russian),
        Translation("Українська (Україна)", Language.Ukrainian),
        Translation("العربية (المملكة العربية السعودية)", Language.Arabic),
        Translation("简体中文", Language.ChineseSimplified),
    )
