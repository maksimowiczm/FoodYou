package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.create

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.ProductForm
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.ProductFormState
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.rememberProductFormState
import com.maksimowiczm.foodyou.ui.component.BackHandler
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateProductDialog(
    onClose: () -> Unit,
    onSuccess: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val latestOnSuccess by rememberUpdatedState(onSuccess)
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is CreateProductState.ProductCreated -> latestOnSuccess(state.productId)
            CreateProductState.CreatingProduct,
            CreateProductState.Nothing,
            CreateProductState.Error -> Unit
        }
    }

    CreateProductDialog(
        onClose = onClose,
        onCreate = viewModel::onCreateProduct,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductDialog(
    onClose: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val form = rememberProductFormState(null)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }

    val handleClose = {
        if (form.isDirty) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = form.isDirty
    ) {
        showDiscardDialog = true
    }

    Scaffold(
        // Use WindowInsets to prevent spring animation when keyboard is hiding
        modifier = modifier.padding(WindowInsets.ime.asPaddingValues()),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = handleClose
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.action_close)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.headline_create_product)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { onCreate(form) },
                        enabled = form.isValid
                    ) {
                        Text(
                            text = stringResource(Res.string.action_create)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ProductForm(
            state = form,
            paddingValues = paddingValues,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
private fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(Res.string.question_discard_product))
        }
    )
}

@Preview
@Composable
private fun CreateProductScreenPreview() {
    FoodYouTheme {
        CreateProductDialog(
            onClose = {},
            onCreate = {}
        )
    }
}
