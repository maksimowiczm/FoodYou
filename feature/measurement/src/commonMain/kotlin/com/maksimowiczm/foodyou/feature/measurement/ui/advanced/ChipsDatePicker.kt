package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_choose_other_date
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ChipsDatePicker(
    dates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onChooseDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current

    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dates.forEach { date ->
                InputChip(
                    selected = date == selectedDate,
                    onClick = { onDateChange(date) },
                    label = { Text(dateFormatter.formatDateShort(date)) }
                )
            }

            InputChip(
                selected = false,
                onClick = onChooseDate,
                label = { Text(stringResource(Res.string.action_choose_other_date)) }
            )
        }
    }
}
