package com.maksimowiczm.foodyou.infrastructure.android

import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maksimowiczm.foodyou.data.SecurityPreferences
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.android.get

@RunWith(AndroidJUnit4::class)
class FoodYouMainActivityTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSecureFlag() {
        launchActivity<FoodYouMainActivity>().use {
            it.onActivity { activity ->
                with(activity) {
                    val dataStore = get<DataStore<Preferences>>()

                    lifecycleScope.launch {
                        dataStore.set(SecurityPreferences.hideContent to true)
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == FLAG_SECURE)

                        dataStore.set(SecurityPreferences.hideContent to false)
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == 0)
                    }
                }
            }
        }
    }
}
