plugins { alias(libs.plugins.foodYouLibrary) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.sponsorship" }

    val xcfName = "core:sponsorshipKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies { implementation(libs.androidx.paging.common) }
}
