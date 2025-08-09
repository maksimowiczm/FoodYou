package com.maksimowiczm.foodyou.feature.food.shared.ui.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_clear
import foodyou.app.generated.resources.action_scan_barcode
import foodyou.app.generated.resources.action_search
import foodyou.app.generated.resources.ic_barcode_scanner
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoodSearchBarInputField(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearch: (String?) -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = onSearch,
        modifier = modifier,
        placeholder = { Text(stringResource(Res.string.action_search)) },
        leadingIcon = {
            if (searchBarState.targetValue == SearchBarValue.Expanded) {
                ArrowBackIconButton(
                    onClick = { coroutineScope.launch { searchBarState.animateToCollapsed() } }
                )
            } else {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            Row {
                if (textFieldState.text.isEmpty()) {
                    IconButton(onBarcodeScanner) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_barcode_scanner),
                            contentDescription = stringResource(Res.string.action_scan_barcode),
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            if (searchBarState.targetValue == SearchBarValue.Collapsed) {
                                onSearch(null)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(Res.string.action_clear),
                        )
                    }
                }
            }
        },
    )
}
