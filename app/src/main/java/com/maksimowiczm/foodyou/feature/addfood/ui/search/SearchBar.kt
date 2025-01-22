package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.component.FullScreenDialog
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var query by rememberSaveable { mutableStateOf("") }

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenDialog(
            onDismissRequest = { showBarcodeScanner = false }
        ) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    query = it
                    onSearch(query)
                    showBarcodeScanner = false
                }
            )
        }
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    query = it.trim()
                    onSearch(query)
                    focusManager.clearFocus()
                },
                expanded = false,
                onExpandedChange = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(
                            onClick = {
                                query = ""
                                onSearch(query)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.action_clear)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                showBarcodeScanner = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_qr_code_scanner_24),
                                contentDescription = stringResource(R.string.action_open_barcode_scanner)
                            )
                        }
                    }
                },
                placeholder = {
                    Text(stringResource(R.string.action_search))
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        // Add padding to the bottom of the SearchBar because it has random top padding
        modifier = modifier.padding(bottom = 8.dp),
        windowInsets = WindowInsets(0),
        content = {}
    )
}

@PreviewLightDark
@Composable
private fun SearchBarPreview() {
    FoodYouTheme {
        SearchBar(
            onSearch = {}
        )
    }
}
