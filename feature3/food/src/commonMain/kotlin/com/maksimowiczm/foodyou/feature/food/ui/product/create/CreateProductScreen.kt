package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.DiscardDialog
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductForm
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductFormState
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateProductScreen(
    state: ProductFormState,
    onBack: () -> Unit,
    onCreate: (ProductFormState) -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val handleBack = {
        if (!state.isModified) {
            onBack()
        } else {
            showDiscardDialog = true
        }
    }
    BackHandler(
        enabled = state.isModified
    ) {
        showDiscardDialog = true
    }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onDiscard = {
                showDiscardDialog = false
                onBack()
            }
        ) {
            Text(stringResource(Res.string.question_discard_product))
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_create_product)) },
                navigationIcon = { ArrowBackIconButton(handleBack) },
                actions = {
                    FilledIconButton(
                        onClick = { onCreate(state) },
                        enabled = state.isValid
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
                AssistChip(
                    onClick = onDownload,
                    label = { Text(stringResource(Res.string.action_download_product)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                )
            }

            item {
                ProductForm(
                    state = state,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}
