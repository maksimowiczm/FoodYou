package com.maksimowiczm.foodyou.feature.settings.language.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.SetTranslationCommand
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveCurrentTranslationQuery
import com.maksimowiczm.foodyou.business.settings.application.query.ObserveTranslationsQuery
import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class LanguageViewModel(queryBus: QueryBus, private val commandBus: CommandBus) :
    ViewModel() {

    private val translationsFlow = queryBus.dispatch<List<Translation>>(ObserveTranslationsQuery)

    val translations =
        translationsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { translationsFlow.first() },
        )

    private val currentTranslationFlow =
        queryBus.dispatch<Translation>(ObserveCurrentTranslationQuery)

    val translation =
        currentTranslationFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { currentTranslationFlow.first() },
        )

    fun selectTranslation(translation: Translation?) {
        viewModelScope.launch {
            commandBus.dispatch(SetTranslationCommand(translation?.languageTag))
        }
    }
}
