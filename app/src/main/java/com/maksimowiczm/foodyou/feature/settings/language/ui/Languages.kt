package com.maksimowiczm.foodyou.feature.settings.language.ui

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

val languages = mapOf(
    // If you'd like to be credited for your translations, please add your name here.
    // "Language (Country)" to Translation(
    //     tag = "language-COUNTRY",
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
        authorsStrings = listOf(
            Author(
                name = "Mateusz Maksimowicz"
            )
        )
    ),
    "Polski (Polska)" to Translation(
        tag = "pl-PL",
        authorsStrings = listOf(
            Author(
                name = "Mateusz Maksimowicz"
            )
        )
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
