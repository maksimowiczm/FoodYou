package com.maksimowiczm.foodyou.app.ui.sponsor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.domain.userpreferences.get
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.common.extension.now
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth

internal class SponsorViewModel(
    private val sponsorRepository: SponsorRepository,
    private val preferencesRepository: UserPreferencesRepository<SponsorshipPreferences>,
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)
    private val isError = MutableStateFlow(false)

    private val yearMonth = MutableStateFlow(LocalDate.now().yearMonth)

    private val messages =
        yearMonth.flatMapLatest { yearMonth ->
            sponsorRepository.observeByYearMonth(yearMonth).map { list ->
                list.map { it.toUiModel() }
            }
        }

    private val goals = observeGoals()

    private val ordering = MutableStateFlow(MessagesOrder.TopFirst)

    val uiState =
        combine(isError, isLoading, yearMonth, messages, ordering, goals) {
                isError,
                isLoading,
                yearMonth,
                messages,
                ordering,
                goals ->
                val totalAmount = messages.sumOf { it.inEuro.toDouble() }
                val goals =
                    goals.map {
                        GoalUiModel(
                            amount = it.amount,
                            title = it.title,
                            description = it.description,
                            fulfilled = totalAmount >= it.amount,
                        )
                    }
                val nextGoal = goals.firstOrNull { !it.fulfilled }
                val remainingForNextGoal = nextGoal?.let { (it.amount - totalAmount) }

                SponsorScreenUiState(
                    isError = isError,
                    isLoading = isLoading,
                    yearMonth = yearMonth,
                    amount = totalAmount,
                    remainingForNextGoal = remainingForNextGoal,
                    goals = goals,
                    messages = messages.sortedWith(ordering.comparator()),
                    messagesOrder = ordering,
                )
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    SponsorScreenUiState(
                        isError = false,
                        isLoading = true,
                        yearMonth = yearMonth.value,
                        amount = 0.0,
                        remainingForNextGoal = null,
                        goals = emptyList(),
                        messages = emptyList(),
                        messagesOrder = MessagesOrder.TopFirst,
                    ),
            )

    val privacyAccepted =
        preferencesRepository
            .observe()
            .map { it.remoteAllowed }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { preferencesRepository.get().remoteAllowed },
            )

    init {
        viewModelScope.launch { yearMonth.collectLatest { yearMonth -> refresh(yearMonth) } }
    }

    fun refresh(yearMonth: YearMonth) {
        viewModelScope.launch {
            isLoading.value = true
            isError.value = false
            try {
                sponsorRepository.requestSync(yearMonth)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                }
                isError.value = true
            } finally {
                isLoading.value = false
            }
        }
    }

    fun setPrivacyAccepted(accepted: Boolean) {
        viewModelScope.launch {
            preferencesRepository.update { copy(remoteAllowed = accepted) }
            if (accepted) {
                refresh(yearMonth.value)
            }
        }
    }

    fun changeMessagesOrder(order: MessagesOrder) {
        ordering.value = order
    }

    fun previousMonth() {
        yearMonth.value = yearMonth.value.minusMonth()
    }

    fun nextMonth() {
        yearMonth.value = yearMonth.value.plusMonth()
    }

    fun refresh() {
        refresh(yearMonth.value)
    }
}

private fun MessagesOrder.comparator(): Comparator<SponsorMessageUiModel> =
    when (this) {
        MessagesOrder.TopFirst -> compareByDescending { it.inEuro.toDouble() }
        MessagesOrder.NewestFirst -> compareByDescending { it.dateTime }
    }

private fun Sponsorship.toUiModel(): SponsorMessageUiModel =
    SponsorMessageUiModel(
        id = id,
        sponsor = sponsorName,
        message = message,
        method = method,
        dateTime = dateTime,
        amount = amount,
        currency = currency,
        inEuro = inEuro,
    )

private class Goal(val amount: Int, val title: String, val description: String)

// TODO Replace with real implementation
private fun observeGoals(): Flow<List<Goal>> =
    flowOf(
        listOf(
            Goal(5, "App maintenance", "Covers small technical costs"),
            Goal(
                60,
                "Gym membership",
                "Monthly gym pass so I can stay healthy and energized while continuing to work on the project",
            ),
            Goal(
                350,
                "Food and groceries",
                "Support essential living expenses by covering a month’s worth of groceries and meals",
            ),
        )
    )
