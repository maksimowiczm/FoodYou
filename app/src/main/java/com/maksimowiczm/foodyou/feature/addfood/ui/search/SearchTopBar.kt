package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchState: SearchState,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = {
            if (searchState.searchQuery.isNotBlank()) {
                Text(
                    text = searchState.searchQuery,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            } else {
                Text(
                    text = searchState.meal.stringResource()
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = {
                    onCloseClick()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_go_back)
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = searchState.searchQuery.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        searchState.onSearch("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.action_clear_search_query)
                    )
                }
            }
        },
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SearchTopBarPreview() {
    SearchTopBar(
        searchState = rememberSearchState(
            meal = Meal.Breakfast,
            initialIsLoading = false,
            initialIsError = false,
            initialData = emptyList(),
            onQuickRemove = {},
            onQuickAdd = { 0 }
        )
    )
}
