package com.maksimowiczm.foodyou.app.ui.sponsor

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.YearMonth

@Immutable
internal data class SponsorScreenUiState(
    val isLoading: Boolean,
    val yearMonth: YearMonth,
    val amount: Double,
    val remainingForNextGoal: Double?,
    val goals: List<GoalUiModel>,
    val messages: List<SponsorMessageUiModel>,
    val messagesOrder: MessagesOrder,
) {
    /**
     * Index of the current goal (0-based). If all goals are fulfilled, it will be equal to the size
     * of the goals list.
     */
    val progressStep = goals.indexOfFirst { !it.fulfilled }
}

@Immutable
internal data class GoalUiModel(
    val amount: Int,
    val title: String,
    val description: String,
    val fulfilled: Boolean,
)

@Immutable
internal data class SponsorMessageUiModel(
    val id: Long,
    val sponsor: String?,
    val message: String?,
    val method: String,
    val dateTime: LocalDateTime,
    val amount: String,
    val currency: String,
    val inEuro: String,
)

internal enum class MessagesOrder {
    NewestFirst,
    TopFirst;

    fun toggle() = if (this == NewestFirst) TopFirst else NewestFirst
}
