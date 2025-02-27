package com.maksimowiczm.foodyou.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

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
            Text(stringResource(R.string.headline_launcher_icon_by_icons8))
        },
        modifier = modifier.clickable { onOpenIcons8() },
        supportingContent = {
            Text(stringResource(R.string.neutral_see_other_awesome_icons_here))
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_sushi),
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
