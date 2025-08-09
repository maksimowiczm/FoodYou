package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import com.maksimowiczm.foodyou.ui.FoodYouApp

class MainActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { FoodYouApp() }
    }
}
