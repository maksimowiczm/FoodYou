package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    searchState: SearchState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Request focus when the view is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var textFieldValue by rememberSaveable(
        searchState.searchQuery,
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(
            TextFieldValue(
                text = searchState.searchQuery,
                selection = TextRange(searchState.searchQuery.length)
            )
        )
    }

    val leadingIcon = @Composable {
        IconButton(
            onClick = {
                searchState.onSearchClose()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_go_back)
            )
        }
    }
    val trailingIcon = @Composable {
        AnimatedVisibility(
            visible = textFieldValue.text.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IconButton(
                onClick = {
                    textFieldValue = TextFieldValue("")
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.action_clear_search_query)
                )
            }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors()
    val localStyle = LocalTextStyle.current
    val mergedStyle = localStyle.merge(TextStyle(color = LocalContentColor.current))

    SearchBar(
        modifier = modifier.focusRequester(focusRequester),
        inputField = {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        searchState.onSearch(textFieldValue.text)
                    }
                ),
                textStyle = mergedStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox =
                @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = textFieldValue.text,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        placeholder = { Text(stringResource(R.string.action_search)) },
                        leadingIcon = leadingIcon.let { leading ->
                            { Box(Modifier.offset(x = 4.dp)) { leading() } }
                        },
                        trailingIcon = trailingIcon.let { trailing ->
                            { Box(Modifier.offset(x = (-4).dp)) { trailing() } }
                        },
                        shape = SearchBarDefaults.inputFieldShape,
                        colors = colors,
                        container = {}
                    )
                }
            )
        },
        expanded = true,
        onExpandedChange = {
            if (!it) {
                searchState.onSearchClose()
            }
        }
    ) {
        val recentQueries by searchState.getRecentQueries().collectAsStateWithLifecycle(emptyList())

        LazyColumn {
            items(recentQueries) { query ->
                ListItem(
                    modifier = Modifier.clickable { searchState.onSearch(query) },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Companion.Transparent
                    ),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_history_24),
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(text = query)
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete)
                            )
                        }
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchViewPreview() {
    val data = ProductWithWeightMeasurementPreviewParameter().values.toList()
    FoodYouTheme {
        Surface {
            SearchView(
                searchState = rememberSearchState(
                    meal = Meal.Breakfast,
                    initialIsLoading = true,
                    initialIsError = true,
                    initialData = data,
                    onQuickRemove = {},
                    onQuickAdd = { 0 },
                    getRecentQueries = { flowOf(listOf("Apple", "Banana", "Cherry")) }
                )
            )
        }
    }
}
