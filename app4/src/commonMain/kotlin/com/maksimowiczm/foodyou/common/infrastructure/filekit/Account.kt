package com.maksimowiczm.foodyou.common.infrastructure.filekit

import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir

fun Account.directory(): PlatformFile = localAccountId.directory()

fun LocalAccountId.directory(): PlatformFile = FileKit.filesDir / value
