package com.maksimowiczm.foodyou.features.onboarding

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3.FoodYou3ImportState
import com.maksimowiczm.foodyou.shared.ui.component.UiProfileAvatar
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Immutable
internal interface ImportState {
    @Serializable @Immutable data object Skip : ImportState
}

@Stable
internal class OnboardingState(
    val nameTextFieldState: TextFieldState,
    avatarState: MutableState<UiProfileAvatar>,
    allowOpenFoodFactsState: MutableState<Boolean>,
    allowFoodDataCentralState: MutableState<Boolean>,
    importState: MutableState<ImportState>,
) {
    var profileAvatar: UiProfileAvatar by avatarState
    var allowOpenFoodFacts: Boolean by allowOpenFoodFactsState
    var allowFoodDataCentral: Boolean by allowFoodDataCentralState

    var importState by importState

    val isValid by derivedStateOf { nameTextFieldState.text.isNotBlank() }
}

private val importStateSerializersModule = SerializersModule {
    polymorphic(ImportState::class) {
        subclass(FoodYou3ImportState::class, FoodYou3ImportState.serializer())
        subclass(ImportState.Skip::class, ImportState.Skip.serializer())
    }
}

@Composable
internal fun rememberOnboardingState(): OnboardingState {
    val nameTextFieldState = rememberTextFieldState()
    val avatarState = rememberSerializable {
        mutableStateOf<UiProfileAvatar>(
            UiProfileAvatar.Predefined(Profile.Avatar.Predefined.Variant.Person)
        )
    }
    val allowOpenFoodFactsState = rememberSaveable { mutableStateOf(false) }
    val allowFoodDataCentralState = rememberSaveable { mutableStateOf(false) }
    val importState =
        rememberSerializable(
            configuration =
                SavedStateConfiguration { serializersModule = importStateSerializersModule },
            stateSerializer = PolymorphicSerializer(ImportState::class),
        ) {
            mutableStateOf(ImportState.Skip)
        }

    return remember {
        OnboardingState(
            nameTextFieldState = nameTextFieldState,
            avatarState = avatarState,
            allowOpenFoodFactsState = allowOpenFoodFactsState,
            allowFoodDataCentralState = allowFoodDataCentralState,
            importState = importState,
        )
    }
}
