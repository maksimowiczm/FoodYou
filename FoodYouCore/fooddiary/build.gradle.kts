plugins { alias(libs.plugins.foodYouLibrary) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.fooddiary" }

    val xcfName = "core:fooddiaryKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }
}
