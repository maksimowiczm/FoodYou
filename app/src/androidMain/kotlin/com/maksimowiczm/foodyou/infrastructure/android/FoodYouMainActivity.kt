package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import org.koin.compose.koinInject

class FoodYouMainActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val database = koinInject<FoodYouDatabase>()
            database.foodLocalDataSource.observeProducts().collectAsStateWithLifecycle(null)

//            FoodYouApp()
        }
    }
}
