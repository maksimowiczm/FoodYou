plugins { alias(libs.plugins.business) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.food" }

    val xcfName = "business:foodKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.core.shared)
        implementation(libs.core.food)
        implementation(libs.androidx.paging.common)
    }
}
