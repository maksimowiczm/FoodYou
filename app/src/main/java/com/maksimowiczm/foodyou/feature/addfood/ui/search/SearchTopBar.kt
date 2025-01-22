package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.ui.res.stringResource
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    meal: Meal,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {
    CenterAlignedTopAppBar(
        title = {
            Text(meal.stringResource())
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onCloseClick
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.action_close)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchTopBarPreview() {
    FoodYouTheme {
        SearchTopBar(
            meal = Meal.Breakfast,
            onCloseClick = {}
        )
    }
}
