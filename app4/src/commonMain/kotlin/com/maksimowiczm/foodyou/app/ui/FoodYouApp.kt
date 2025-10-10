package com.maksimowiczm.foodyou.app.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.app.ui.common.theme.FoodYouTheme
import com.maksimowiczm.foodyou.app.ui.onboarding.Onboarding

@Composable
fun FoodYouApp() {
    var onboardingFinished by rememberSaveable { mutableStateOf(false) }
    FoodYouTheme {
        Surface {
            if (!onboardingFinished) {
                Onboarding(onFinish = { onboardingFinished = true })
            }
        }
    }
}
