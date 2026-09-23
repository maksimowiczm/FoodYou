package com.maksimowiczm.foodyou.preferences.domain

import com.maksimowiczm.foodyou.common.domain.Language
import kotlin.jvm.JvmInline

@JvmInline value class LanguagePreference(val language: Language?) : UserPreferences
