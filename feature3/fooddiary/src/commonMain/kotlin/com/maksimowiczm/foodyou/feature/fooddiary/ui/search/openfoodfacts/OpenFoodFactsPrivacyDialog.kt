package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.foundation.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OpenFoodFactsPrivacyDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onConfirm) {
                Text(stringResource(Res.string.action_agree))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Image(painterResource(Res.drawable.openfoodfacts_logo), null)
        },
        title = {
            Text(stringResource(Res.string.headline_open_food_facts))
        },
        text = { DialogText() }
    )
}

@Composable
private fun DialogText(modifier: Modifier = Modifier) {
    val iterator =
        stringResource(Res.string.description_open_food_facts_privacy_dialog_text).iterator()

    val text = remember(iterator) {
        buildAnnotatedString {
            while (iterator.hasNext()) {
                val char = iterator.nextChar()

                if (char != '{') {
                    append(char)
                    continue
                }

                val label = iterator.readUntil(':')
                val link = iterator.readUntil('}')

                withLink(LinkAnnotation.Url(link)) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(label)
                    }
                }
            }
        }
    }

    Text(
        text = text,
        modifier = modifier
    )
}

private fun CharIterator.readUntil(delimiter: Char): String = buildString {
    while (hasNext()) {
        val ch = nextChar()
        if (ch == delimiter) break
        append(ch)
    }
}
