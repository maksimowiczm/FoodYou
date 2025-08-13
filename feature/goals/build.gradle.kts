plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.goals" }

    val xcfName = "feature:goalsKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.fooddiary)
        implementation(projects.business.settings)
        implementation(projects.business.shared)
        implementation(libs.kotlinx.datetime)
    }
}
