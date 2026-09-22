package com.maksimowiczm.foodyou

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.preferences.domain.FoodDiaryEntryTimestampsPreference
import com.maksimowiczm.foodyou.preferences.domain.HideScreenPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import com.maksimowiczm.foodyou.preferences.infrastructure.LanguagePreferenceProvider
import com.maksimowiczm.foodyou.shared.ui.utility.DateFormatterImpl
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.UIFeatureFlags
import com.maksimowiczm.foodyou.shared.ui.utility.UtilityProvider
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

abstract class FoodYouAbstractActivity : AppCompatActivity() {
    private val languagePreferenceProvider: LanguagePreferenceProvider by inject()
    private val foodNameSelector: FoodNameSelector by inject()
    private val appConfig: FoodYouConfig by inject()
    private val blobResolver: BlobResolver by inject()
    private val accountService: AccountService by inject()
    private val featureFlags: StateFlow<UIFeatureFlags> by
        lazy(LazyThreadSafetyMode.NONE) {
            combine(
                    accountService.observe().filterNotNull(),
                    userPreferencesRepository.observe<FoodDiaryEntryTimestampsPreference>(),
                ) { account, timestampsPref ->
                    UIFeatureFlags(
                        singleProfileMode = account.singleProfileMode,
                        foodDiaryEntryTimestamps = timestampsPref.enabled,
                    )
                }
                .stateIn(
                    scope = lifecycleScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = UIFeatureFlags(),
                )
        }
    private val dataStore: DataStore<Preferences> by inject()
    private val userPreferencesRepository: UserPreferencesRepository by inject()

    fun setContent(content: @Composable () -> Unit) {
        val dateFormatter = DateFormatterImpl(this)
        with(this as AppCompatActivity) {
            setContent {
                val uiFeatureFlags by featureFlags.collectAsStateWithLifecycle()
                UtilityProvider(
                    dateFormatter = dateFormatter,
                    foodNameSelector = foodNameSelector,
                    appConfig = appConfig,
                    blobResolver = blobResolver,
                    uiFeatureFlags = uiFeatureFlags,
                    dataStore = dataStore,
                    content = content,
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(languagePreferenceProvider)
        lifecycleScope.launch { observeShowContentSecurity() }
        FileKit.init(this)
        enableEdgeToEdge()
    }

    override fun onDestroy() {
        lifecycle.removeObserver(languagePreferenceProvider)
        super.onDestroy()
    }

    private suspend fun observeShowContentSecurity() {
        userPreferencesRepository
            .observe<HideScreenPreference>()
            .map { it.hideScreen }
            .collectLatest {
                if (it) {
                    window.setFlags(FLAG_SECURE, FLAG_SECURE)
                } else {
                    window.clearFlags(FLAG_SECURE)
                }
            }
    }
}
