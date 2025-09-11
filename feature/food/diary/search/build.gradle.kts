plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.diary.search" }

    val xcfName = "feature:food:diary:searchKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.feature.food.shared)

        implementation(libs.compose.shimmer)
        implementation(libs.kotlinx.datetime)
    }
}
