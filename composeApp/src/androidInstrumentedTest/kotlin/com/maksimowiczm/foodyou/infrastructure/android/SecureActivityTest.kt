package com.maksimowiczm.foodyou.infrastructure.android

import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.launchActivity
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.junit.Test
import org.koin.android.ext.android.get

class SecureActivityTest {
    private inline fun <reified A : FoodYouAbstractActivity> testSecureFlag() {
        launchActivity<A>().use {
            it.onActivity { activity ->
                with(activity) {
                    val commandBus: CommandBus = get()

                    // Run on same thread as the activity
                    lifecycleScope.launch {
                        commandBus.dispatch(PartialSettingsUpdateCommand(secureScreen = true))

                        // Yield just to be safe
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == FLAG_SECURE)

                        commandBus.dispatch(PartialSettingsUpdateCommand(secureScreen = false))

                        // Yield just to be safe
                        yield()

                        assert((window.attributes.flags and FLAG_SECURE) == 0)
                    }
                }
            }
        }
    }

    @Test fun testSecureMainActivity() = testSecureFlag<MainActivity>()

    @Test fun testSecureShareProductActivity() = testSecureFlag<ShareProductActivity>()

    @Test fun testSecureCrashReportActivity() = testSecureFlag<CrashReportActivity>()
}
