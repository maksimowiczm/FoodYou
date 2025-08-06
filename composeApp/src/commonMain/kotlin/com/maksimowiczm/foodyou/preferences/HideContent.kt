//package com.maksimowiczm.foodyou.preferences
//
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference
//
//class HideContent(dataStore: DataStore<Preferences>) :
//    DataStoreUserPreference<Boolean, Boolean>(
//        dataStore = dataStore,
//        key = booleanPreferencesKey("security_hide_content")
//    ) {
//    override fun Boolean?.toValue() = this ?: false
//    override fun Boolean.toStore() = this
//}
