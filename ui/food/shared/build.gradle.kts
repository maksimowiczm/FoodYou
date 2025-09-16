plugins { alias(libs.plugins.ui) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.food.shared" }

    val xcfName = "ui:food:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)

        implementation(libs.core.shared)
        implementation(libs.core.food)

        implementation(libs.compose.shimmer)
        implementation(libs.androidx.paging.common)
        implementation(libs.kotlinx.datetime)
    }
}
