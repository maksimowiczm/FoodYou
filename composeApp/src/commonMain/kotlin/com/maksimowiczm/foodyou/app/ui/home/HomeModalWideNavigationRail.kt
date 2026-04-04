package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.home.search.CollectionFilter
import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchState
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeModalWideNavigationRail(
    homeSearchState: HomeSearchState,
    collections: List<CollectionFilter>,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    ModalWideNavigationRail(
        modifier = modifier,
        header = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 24.dp),
            ) {
                IconButton(
                    onClick = { scope.launch { homeSearchState.railState.collapse() } },
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuOpen,
                        contentDescription = stringResource(Res.string.action_close),
                    )
                }
                FloatingActionButton(
                    onClick = onCreate,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(Res.string.action_create),
                    )
                }
            }
        },
        hideOnCollapse = true,
        expandedShape =
            MaterialTheme.shapes.large.copy(topStart = CornerSize(0), bottomStart = CornerSize(0)),
        state = homeSearchState.railState,
        contentPadding =
            PaddingValues(
                top = 8.dp,
                bottom = WideNavigationRailDefaults.ContentPadding.calculateBottomPadding(),
            ),
    ) {
        val onRailItemClick: (CollectionFilter) -> Unit = {
            scope.launch { homeSearchState.goToSearch(collectionFilter = it) }
        }
        Text(
            modifier = Modifier.padding(start = 40.dp).padding(vertical = 16.dp),
            text = stringResource(Res.string.headline_collections),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        collections.forEach { collection ->
            WideNavigationRailItem(
                selected = false,
                onClick = { onRailItemClick(collection) },
                icon = { collection.Icon(Modifier.size(24.dp)) },
                label = { Text(collection.stringResource()) },
                railExpanded = true,
            )
        }
    }
}
