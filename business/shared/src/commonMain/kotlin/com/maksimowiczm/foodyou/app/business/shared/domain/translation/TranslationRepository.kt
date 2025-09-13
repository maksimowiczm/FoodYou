package com.maksimowiczm.foodyou.app.business.shared.domain.translation

import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    fun observe(): Flow<List<Translation>>

    fun observeCurrent(): Flow<Translation>

    /**
     * Sets the app translation. If `null` is provided, the system default language will be used.
     */
    suspend fun setTranslation(translation: Translation?)
}
