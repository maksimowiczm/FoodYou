package com.maksimowiczm.foodyou.app.ui.food.product.update

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardDialog
import com.maksimowiczm.foodyou.app.ui.food.product.ProductForm
import com.maksimowiczm.foodyou.app.ui.food.product.rememberProductFormState
import com.maksimowiczm.foodyou.common.compose.extension.LaunchedCollectWithLifecycle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UpdateProductScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    viewModel: UpdateProductViewModel,
    modifier: Modifier = Modifier,
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
        NavigationEventHandler(
            state = rememberNavigationEventState(NavigationEventInfo.None),
            isBackEnabled = productForm.isModified,
            onBackCompleted = { showDiscardDialog = true },
        )
        if (showDiscardDialog) {
            DiscardDialog(
                onDismissRequest = { showDiscardDialog = false },
                onDiscard = {
                    showDiscardDialog = false
                    onBack()
                },
            ) {
                Text(stringResource(Res.string.question_discard_changes))
            }
        }

        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.headline_edit_product)) },
                    navigationIcon = { ArrowBackIconButton(handleBack) },
                    actions = {
                        FilledIconButton(
                            onClick = { viewModel.updateProduct(productForm) },
                            enabled = productForm.isValid,
                        ) {
                            Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize()
                        .imePadding()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues,
            ) {
                item {
                    ProductForm(
                        state = productForm,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
