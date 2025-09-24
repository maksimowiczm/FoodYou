package com.maksimowiczm.foodyou.app.infrastructure.android

import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.launchActivity
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.settings.domain.entity.Settings
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.junit.Test
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

class SecureActivityTest {
    private inline fun <reified A : FoodYouAbstractActivity> testSecureFlag() {
        launchActivity<A>().use {
            it.onActivity { activity ->
                with(activity) {
                    val settingsRepository: UserPreferencesRepository<Settings> =
                        get(named(Settings::class.qualifiedName!!))

                    // Run on same thread as the activity
                    lifecycleScope.launch {
                        settingsRepository.update { copy(secureScreen = true) }

                        // Yield just to be safe
                        yield()

                        assert(
                            (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) ==
                                WindowManager.LayoutParams.FLAG_SECURE
                        )

                        settingsRepository.update { copy(secureScreen = false) }

                        // Yield just to be safe
                        yield()

                        assert(
                            (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) ==
                                0
                        )
                    }
                }
            }
        }
    }

    @Test fun testSecureMainActivity() = testSecureFlag<MainActivity>()

    @Test fun testSecureShareProductActivity() = testSecureFlag<ShareProductActivity>()

    @Test fun testSecureCrashReportActivity() = testSecureFlag<CrashReportActivity>()
}
