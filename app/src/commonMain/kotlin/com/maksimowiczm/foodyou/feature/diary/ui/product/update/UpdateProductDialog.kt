package com.maksimowiczm.foodyou.feature.diary.ui.product.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.ui.product.ProductForm
import com.maksimowiczm.foodyou.feature.diary.ui.product.ProductFormState
import com.maksimowiczm.foodyou.feature.diary.ui.product.rememberProductFormState
import com.maksimowiczm.foodyou.ui.component.BackHandler
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UpdateProductDialog(
    onClose: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val latestOnSuccess by rememberUpdatedState(onSuccess)
    LaunchedEffect(uiState) {
        when (uiState) {
            UpdateProductState.Loading,
            is UpdateProductState.ProductReady,
            is UpdateProductState.UpdatingProduct -> Unit

            is UpdateProductState.ProductUpdated -> latestOnSuccess()
        }
    }

    UpdateProductDialog(
        state = uiState,
        onNavigateBack = onClose,
        onUpdateProduct = viewModel::updateProduct,
        modifier = modifier
    )
}

@Composable
private fun UpdateProductDialog(
    state: UpdateProductState,
    onNavigateBack: () -> Unit,
    onUpdateProduct: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        UpdateProductState.Loading -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        is UpdateProductState.WithProduct -> UpdateProductDialog(
            product = state.product,
            onClose = onNavigateBack,
            onUpdate = onUpdateProduct,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProductDialog(
    product: Product,
    onClose: () -> Unit,
    onUpdate: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberProductFormState(
        product = product
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }

    val handleClose = {
        if (state.isDirty) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = state.isDirty
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
                        text = stringResource(Res.string.headline_edit_product)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { onUpdate(state) },
                        enabled = state.isValid
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ProductForm(
            state = state,
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
            Text(stringResource(Res.string.question_discard_changes))
        }
    )
}
