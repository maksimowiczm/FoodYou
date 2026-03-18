package com.maksimowiczm.foodyou.common.infrastructure.filekit

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir

fun accountDirectory(): PlatformFile = FileKit.filesDir / "DEFAULT"
