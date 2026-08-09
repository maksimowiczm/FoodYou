package com.maksimowiczm.foodyou.features.userproduct.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.features.userproduct.FillSuggestedFieldsDialog
import com.maksimowiczm.foodyou.features.userproduct.ProductForm
import com.maksimowiczm.foodyou.features.userproduct.ProductFormState
import com.maksimowiczm.foodyou.features.userproduct.rememberProductFormState
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditProductScreen(
    identity: UserProductIdentity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditProductViewModel = koinViewModel { parametersOf(identity) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            EditProductEvent.Edited -> onEdit()
        }
    }

    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val product by viewModel.product.collectAsStateWithLifecycle()

    if (product != null) {
        val formState = rememberProductFormState(product)

        EditProductScreen(
            onBack = onBack,
            onSave = viewModel::save,
            formState = formState,
            isLocked = isLocked,
            modifier = modifier,
        )
    }
}

@Composable
private fun EditProductScreen(
    onBack: () -> Unit,
    onSave: (ProductFormState) -> Unit,
    formState: ProductFormState,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack)
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = !isLocked && formState.isModified,
        onBackCompleted = { showDiscardDialog = true },
    )
    val pleaseWaitStr = stringResource(Res.string.headline_please_wait)
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isLocked,
        onBackCompleted = {
            scope.launch { snackbarHostState.showSnackbar(message = pleaseWaitStr) }
        },
    )

    val focusRequester = remember { FocusRequester() }
    var showFillSuggestedFieldsDialog by rememberSaveable { mutableStateOf(false) }
    if (showFillSuggestedFieldsDialog) {
        FillSuggestedFieldsDialog(
            onDismissRequest = { showFillSuggestedFieldsDialog = false },
            onConfirm = {
                showFillSuggestedFieldsDialog = false
                focusRequester.requestFocus()
            },
            onSkip = {
                showFillSuggestedFieldsDialog = false
                onSave(formState)
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_edit_product)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = {
                            if (formState.isModified) showDiscardDialog = true else onBack()
                        },
                        enabled = !isLocked,
                    )
                },
                actions = {
                    FilledIconButton(
                        onClick = {
                            if (!formState.hasSuggestedFieldsFilled) {
                                showFillSuggestedFieldsDialog = true
                            } else {
                                onSave(formState)
                            }
                        },
                        shapes = IconButtonDefaults.shapes(),
                        modifier =
                            Modifier.size(
                                    IconButtonDefaults.smallContainerSize(
                                        IconButtonDefaults.IconButtonWidthOption.Wide
                                    )
                                )
                                .then(if (isLocked) Modifier.shimmer() else Modifier),
                        enabled = formState.isValid && !isLocked,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.action_save),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.imePadding()
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                ProductForm(
                    state = formState,
                    isLocked = isLocked,
                    macroFocusRequester = focusRequester,
                )
            }
        }
    }
}
