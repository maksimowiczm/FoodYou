package com.maksimowiczm.foodyou.screenshot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

@OptIn(ExperimentalTestApi::class) expect suspend fun ComposeUiTest.capture(screenshot: Screenshot)
