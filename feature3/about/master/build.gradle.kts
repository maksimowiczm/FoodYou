plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.about.master" }

    val xcfName = "feature:aboutKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.settings)
        implementation(libs.kotlinx.datetime)
    }
}
