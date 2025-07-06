package com.maksimowiczm.foodyou.feature.food.ui.product.update

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductForm
import com.maksimowiczm.foodyou.feature.food.ui.product.rememberProductFormState
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpdateProductScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    viewModel: UpdateProductScreenViewModel,
    modifier: Modifier = Modifier
) {
    val latestOnUpdate by rememberUpdatedState(onUpdate)
    LaunchedCollectWithLifecycle(viewModel.events) { event ->
        when (event) {
            UpdateProductEvent.Updated -> latestOnUpdate()
        }
    }

    val product = viewModel.product.collectAsStateWithLifecycle().value

    if (product == null) {
        // TODO loading state
        return
    } else {
        val productForm = rememberProductFormState(product)
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
        val handleBack = {
            if (!productForm.isModified) {
                onBack()
            } else {
                showDiscardDialog = true
            }
        }
        BackHandler(
            enabled = productForm.isModified
        ) {
            showDiscardDialog = true
        }
        if (showDiscardDialog) {
            DiscardDialog(
                onDismissRequest = { showDiscardDialog = false },
                onConfirm = {
                    showDiscardDialog = false
                    onBack()
                }
            )
        }

        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(Res.string.headline_edit_product))
                    },
                    navigationIcon = {
                        ArrowBackIconButton(handleBack)
                    },
                    actions = {
                        FilledIconButton(
                            onClick = {
                                viewModel.updateProduct(productForm)
                            },
                            enabled = productForm.isValid
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Save,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues
            ) {
                item {
                    ProductForm(
                        state = productForm,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    )
                }
            }
        }
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
