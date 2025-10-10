package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.device.domain.PrivacySettings

internal fun MutablePreferences.applyPrivacySettings(
    privacySettings: PrivacySettings
): MutablePreferences = apply {
    this[PrivacySettingsKeys.foodYouServicesAllowed] = privacySettings.foodYouServicesAllowed
}

internal fun Preferences.toPrivacySettings(): PrivacySettings =
    PrivacySettings(
        foodYouServicesAllowed = this[PrivacySettingsKeys.foodYouServicesAllowed] ?: false
    )

private object PrivacySettingsKeys {
    val foodYouServicesAllowed = booleanPreferencesKey("privacy:foodYouServicesAllowed")
}
