plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.shared" }

    val xcfName = "feature3:food:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(projects.business.food)
    }
}
