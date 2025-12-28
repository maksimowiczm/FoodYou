package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.food.component.Icon
import com.maksimowiczm.foodyou.app.ui.food.component.stringResource
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class FoodFilter(val source: Source = DefaultFilter) {

    companion object {
        val DefaultFilter = Source.Recent
    }

    val filterCount: Int
        get() {
            var count = 0

            if (source != DefaultFilter) {
                count++
            }

            return count
        }

    enum class Source {
        Recent,
        YourFood,
        TBCA;

        @Composable
        fun Icon(modifier: Modifier = Modifier.Companion) =
            when (this) {
                Recent ->
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        modifier = modifier,
                    )

                YourFood ->
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = modifier,
                    )

                TBCA -> FoodSource.Type.TBCA.Icon(modifier)
            }

        @Composable
        fun stringResource(): String =
            when (this) {
                Recent -> stringResource(Res.string.headline_recent)
                YourFood -> stringResource(Res.string.headline_your_food)
                TBCA -> stringResource(Res.string.headline_tbca)
            }
    }
}
