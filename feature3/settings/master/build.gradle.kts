plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.settings.master" }

    val xcfName = "feature3:settings:masterKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies { implementation(projects.business.settings) }
}
