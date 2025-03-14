package com.maksimowiczm.foodyou.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * AboutIcons8 is a composable that displays the Icons8 attribution. Must be used in the about
 * screen of the app if the Icons8 icons are used.
 *
 * @link https://icons8.com/license
 *
 * State at 27.02.2025
 */
@Composable
fun AboutIcons8(onOpenIcons8: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_launcher_icon_by_icons8))
        },
        modifier = modifier.clickable { onOpenIcons8() },
        supportingContent = {
            Text(stringResource(Res.string.neutral_see_other_awesome_icons_here))
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_sushi),
                contentDescription = null
            )
        }
    )
}

@Preview
@Composable
private fun AboutIcons8Preview() {
    FoodYouTheme {
        AboutIcons8(
            onOpenIcons8 = {}
        )
    }
}
