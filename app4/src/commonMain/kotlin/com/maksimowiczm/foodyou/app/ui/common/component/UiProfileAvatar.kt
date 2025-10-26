package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person3
import androidx.compose.material.icons.outlined.Person4
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.maksimowiczm.foodyou.account.domain.Profile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.coil.securelyAccessFile
import io.github.vinceglb.filekit.lastModified

@Immutable
sealed interface UiProfileAvatar {
    @Immutable
    data class Predefined(val type: Type) : UiProfileAvatar {
        enum class Type {
            PERSON,
            WOMAN,
            MAN,
            ENGINEER;

            fun toAvatar(): Predefined = Predefined(this)
        }

        @Composable
        override fun Avatar(modifier: Modifier) {
            Icon(
                imageVector =
                    when (type) {
                        Type.PERSON -> Icons.Outlined.Person
                        Type.WOMAN -> Icons.Outlined.Person3
                        Type.MAN -> Icons.Outlined.Person4
                        Type.ENGINEER -> Icons.Outlined.Engineering
                    },
                contentDescription = null,
                modifier = modifier,
            )
        }
    }

    @Immutable
    data class Photo(val uri: String) : UiProfileAvatar {
        @Composable
        override fun Avatar(modifier: Modifier) {
            val platformContext = LocalPlatformContext.current
            val model =
                remember(platformContext, uri) {
                    val file = PlatformFile(uri)
                    // This is to force Coil to reload the image when the file is modified, it kind
                    // of
                    // leaks from infrastructure to UI layer
                    val cacheKey = "${file.absolutePath()}_${file.lastModified()}"

                    ImageRequest.Builder(platformContext)
                        .data(uri)
                        .memoryCacheKey(cacheKey)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build()
                }
            val file = remember(uri) { PlatformFile(uri) }

            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                onState = { it.securelyAccessFile(file) },
            )
        }
    }

    @Composable fun Avatar(modifier: Modifier = Modifier)
}

object ProfileAvatarMapper {
    fun toUiModel(model: Profile.Avatar): UiProfileAvatar =
        when (model) {
            is Profile.Avatar.Photo -> UiProfileAvatar.Photo(model.uri)
            Profile.Avatar.Predefined.Engineer ->
                UiProfileAvatar.Predefined.Type.ENGINEER.toAvatar()

            Profile.Avatar.Predefined.Man -> UiProfileAvatar.Predefined.Type.MAN.toAvatar()
            Profile.Avatar.Predefined.Person -> UiProfileAvatar.Predefined.Type.PERSON.toAvatar()
            Profile.Avatar.Predefined.Woman -> UiProfileAvatar.Predefined.Type.WOMAN.toAvatar()
        }

    fun toModel(uiModel: UiProfileAvatar): Profile.Avatar =
        when (uiModel) {
            is UiProfileAvatar.Photo -> Profile.Avatar.Photo(uiModel.uri)
            is UiProfileAvatar.Predefined ->
                when (uiModel.type) {
                    UiProfileAvatar.Predefined.Type.PERSON -> Profile.Avatar.Predefined.Person
                    UiProfileAvatar.Predefined.Type.WOMAN -> Profile.Avatar.Predefined.Woman
                    UiProfileAvatar.Predefined.Type.MAN -> Profile.Avatar.Predefined.Man
                    UiProfileAvatar.Predefined.Type.ENGINEER -> Profile.Avatar.Predefined.Engineer
                }
        }
}
