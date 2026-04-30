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
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.common.domain.FileUri
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.coil.securelyAccessFile
import io.github.vinceglb.filekit.lastModified

@Composable
fun Profile.Avatar.Avatar(modifier: Modifier = Modifier) =
    when (this) {
        is Profile.Avatar.Photo -> this.Avatar(modifier)
        is Profile.Avatar.Predefined -> this.Avatar(modifier)
    }

@Composable
fun Profile.Avatar.Predefined.Avatar(modifier: Modifier = Modifier) {
    this.variant.Avatar(modifier)
}

@Composable
private fun Profile.Avatar.Predefined.Variant.Avatar(modifier: Modifier = Modifier) {
    Icon(
        imageVector =
            when (this) {
                Profile.Avatar.Predefined.Variant.Person -> Icons.Outlined.Person
                Profile.Avatar.Predefined.Variant.Woman -> Icons.Outlined.Person3
                Profile.Avatar.Predefined.Variant.Man -> Icons.Outlined.Person4
                Profile.Avatar.Predefined.Variant.Engineer -> Icons.Outlined.Engineering
            },
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
fun Profile.Avatar.Photo.Avatar(modifier: Modifier = Modifier) {
    val uri = resolveBlob(digest)
    Avatar(uri, modifier)
}

@Composable
fun UiProfileAvatar.Avatar(modifier: Modifier = Modifier) {
    when (this) {
        is UiProfileAvatar.Predefined -> Avatar(modifier)
        is UiProfileAvatar.Uri -> Avatar(modifier)
    }
}

@Composable
fun UiProfileAvatar.Uri.Avatar(modifier: Modifier = Modifier) {
    Avatar(uri, modifier)
}

@Composable
fun UiProfileAvatar.Predefined.Avatar(modifier: Modifier = Modifier) {
    variant.Avatar(modifier)
}

@Composable
private fun Avatar(uri: FileUri, modifier: Modifier = Modifier) {
    val uri = uri.value
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
