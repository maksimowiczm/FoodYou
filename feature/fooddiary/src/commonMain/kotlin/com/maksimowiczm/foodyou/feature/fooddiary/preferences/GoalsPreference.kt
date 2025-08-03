package com.maksimowiczm.foodyou.feature.fooddiary.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.WeeklyGoals
import kotlinx.serialization.json.Json

/**
 * A list of goals for each day of the week.
 */
internal class GoalsPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<String, WeeklyGoals>(
        dataStore = dataStore,
        key = stringPreferencesKey("fooddiary:goals")
    ) {

    override fun String?.toValue(): WeeklyGoals = try {
        this?.let(Json::decodeFromString) ?: WeeklyGoals.defaultGoals
    } catch (_: Exception) {
        WeeklyGoals.defaultGoals
    }.fillMissingFields()

    override fun WeeklyGoals.toStore() = Json.encodeToString(this)
}
