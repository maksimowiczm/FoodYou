package com.maksimowiczm.foodyou.feature.diary.addfood.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
    onSearch: (String) -> Unit,
    onBack: (() -> Unit)?,
    onClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBarDefaults.InputField(
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        onSearch = onSearch,
        modifier = modifier,
        placeholder = { Text(stringResource(Res.string.action_search)) },
        leadingIcon = onBack?.let {
            {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            }
        },
        trailingIcon = {
            Row {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(
                        onClick = onClear
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }

                IconButton(
                    onClick = onBarcodeScanner
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = stringResource(Res.string.action_scan_barcode)
                    )
                }
            }
        }
    )
}
