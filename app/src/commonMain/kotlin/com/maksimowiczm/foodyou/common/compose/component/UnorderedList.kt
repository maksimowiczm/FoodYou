package com.maksimowiczm.foodyou.common.compose.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle

@Composable
fun unorderedList(
    items: List<String>,
    prefix: String = "\u2022\t\t",
    textStyle: TextStyle = LocalTextStyle.current,
) = unorderedList(*items.toTypedArray(), prefix = prefix, textStyle = textStyle)

@Composable
fun unorderedList(
    vararg items: String,
    prefix: String = "\u2022\t\t",
    textStyle: TextStyle = LocalTextStyle.current,
): AnnotatedString {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val paragraphStyle =
        remember(textStyle, prefix) {
            val bulletStringWidth =
                textMeasurer.measure(text = prefix, style = textStyle).size.width
            val restLine = with(density) { bulletStringWidth.toSp() }
            ParagraphStyle(textIndent = TextIndent(restLine = restLine))
        }

    return remember(items, prefix, paragraphStyle) {
        buildAnnotatedString {
            items.forEach { text ->
                withStyle(style = paragraphStyle) {
                    append(prefix)
                    append(text)
                }
            }
        }
    }
}
