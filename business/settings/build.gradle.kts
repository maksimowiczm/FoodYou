plugins { alias(libs.plugins.business) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.settings" }

    val xcfName = "business:settingsKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }
}
