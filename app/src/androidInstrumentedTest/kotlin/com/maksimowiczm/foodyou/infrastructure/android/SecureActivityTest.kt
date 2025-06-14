package com.maksimowiczm.foodyou.infrastructure.android

import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.launchActivity
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.data.AppPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.junit.Test
import org.koin.android.ext.android.get

class SecureActivityTest {
    private inline fun <reified A : FoodYouAbstractActivity> testSecureFlag() {
        launchActivity<A>().use {
            it.onActivity { activity ->
                with(activity) {
                    val dataStore = get<DataStore<Preferences>>()

                    // Run on same thread as the activity
                    lifecycleScope.launch {
                        dataStore.set(AppPreferences.hideContent to true)

                        // Yield just to be safe
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == FLAG_SECURE)

                        dataStore.set(AppPreferences.hideContent to false)

                        // Yield just to be safe
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == 0)
                    }
                }
            }
        }
    }

    @Test
    fun testSecureMainActivity() = testSecureFlag<FoodYouMainActivity>()

    @Test
    fun testSecureShareProductActivity() = testSecureFlag<ShareProductActivity>()

    @Test
    fun testSecureCrashReportActivity() = testSecureFlag<CrashReportActivity>()
}
