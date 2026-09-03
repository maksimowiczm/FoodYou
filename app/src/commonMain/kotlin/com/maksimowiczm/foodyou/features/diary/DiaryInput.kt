package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantityInput
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.shared.ui.component.Avatar
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ProfileUiState(val id: ProfileId, val name: String, val avatar: Profile.Avatar)

@Composable
fun DiaryInput(
    selectedType: QuantityType,
    types: List<QuantityType>,
    formField: FormField,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    onSelectType: (QuantityType) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LookaheadScope {
                QuantityInput(
                    selectedType = selectedType,
                    types = types,
                    formField = formField,
                    onSelectType = onSelectType,
                    modifier =
                        Modifier.weight(1f)
                            .animateBounds(
                                this,
                                boundsTransform = { _, _ -> motionScheme.fastEffectsSpec() },
                            ),
                )
                if (profiles.size > 1) {
                    Spacer(Modifier.width(8.dp))
                    ProfileStack(
                        selectedProfiles = selectedProfiles,
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                    )
                }
            }
        }
        if (profiles.size > 1) {
            AnimatedVisibility(
                visible = expanded,
                enter =
                    fadeIn(motionScheme.fastEffectsSpec()) +
                        expandVertically(motionScheme.defaultSpatialSpec()),
                exit =
                    fadeOut(motionScheme.fastEffectsSpec()) +
                        // shrink with effect spec as it looks better
                        shrinkVertically(motionScheme.fastEffectsSpec()),
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    profiles.forEachIndexed { index, profile ->
                        SegmentedListItem(
                            selected = profile in selectedProfiles,
                            onClick = {
                                val selected = selectedProfiles.toMutableList()
                                if (profile in selected && selected.size > 1)
                                    selected.remove(profile)
                                else selected.add(profile)
                                onSelectProfiles(selected.distinct())
                            },
                            shapes =
                                ListItemDefaults.segmentedShapes(
                                    index = index,
                                    count = profiles.size,
                                ),
                            colors =
                                ListItemDefaults.segmentedColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier =
                                        Modifier.size(40.dp)
                                            .clip(CircleShape)
                                            .wrapContentSize(unbounded = true),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    profile.avatar.Avatar(
                                        when (profile.avatar) {
                                            is Profile.Avatar.Photo -> Modifier.size(40.dp)
                                            is Profile.Avatar.Predefined ->
                                                Modifier.padding(8.dp)
                                                    .size(24.dp)
                                                    .wrapContentSize(unbounded = true)
                                        }
                                    )
                                }
                                Text(profile.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LookaheadScope.ProfileStack(
    selectedProfiles: List<ProfileUiState>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    Row(
        modifier =
            modifier.toggleable(
                value = expanded,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onValueChange = { onExpandedChange(!expanded) },
            ),
        horizontalArrangement = Arrangement.spacedBy((-25).dp),
    ) {
        selectedProfiles.forEachIndexed { index, profile ->
            key(profile.id) {
                Box(
                    modifier =
                        Modifier.animateBounds(
                                lookaheadScope = this@ProfileStack,
                                boundsTransform = { _, _ -> motionScheme.fastSpatialSpec() },
                                animateMotionFrameOfReference = true,
                            )
                            .zIndex(selectedProfiles.size - index + 1f)
                            .size(40.dp)
                            .clip(CircleShape)
                            .wrapContentSize(unbounded = true)
                            .shadow(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val avatarModifier =
                        when (profile.avatar) {
                            is Profile.Avatar.Photo -> Modifier.size(40.dp)
                            is Profile.Avatar.Predefined ->
                                Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(8.dp)
                                    .size(24.dp)
                                    .wrapContentSize(unbounded = true)
                        }
                    profile.avatar.Avatar(avatarModifier)
                }
            }
        }
    }
}

@Preview
@Composable
private fun DiaryInputPreview() {
    val previewProfiles =
        listOf(
            ProfileUiState(
                id = ProfileId(Uuid.random()),
                name = "Mateusz",
                avatar = Profile.Avatar.Predefined.Variant.Person.toAvatar(),
            ),
            ProfileUiState(
                id = ProfileId(Uuid.random()),
                name = "Alice",
                avatar = Profile.Avatar.Predefined.Variant.Woman.toAvatar(),
            ),
            ProfileUiState(
                id = ProfileId(Uuid.random()),
                name = "John",
                avatar = Profile.Avatar.Predefined.Variant.Man.toAvatar(),
            ),
            ProfileUiState(
                id = ProfileId(Uuid.random()),
                name = "Jack",
                avatar = Profile.Avatar.Predefined.Variant.Engineer.toAvatar(),
            ),
        )

    PreviewFoodYouTheme {
        DiaryInput(
            selectedType = QuantityType.Gram,
            types = listOf(QuantityType.Gram, QuantityType.Serving),
            formField = FormField(textFieldState = TextFieldState("100")),
            profiles = previewProfiles,
            selectedProfiles = listOf(previewProfiles.first(), previewProfiles.last()),
            onSelectType = {},
            onSelectProfiles = {},
        )
    }
}
