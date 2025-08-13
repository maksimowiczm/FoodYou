plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary {
        namespace =
            "com.maksimowiczm.foodyou.feature.settings.database.swissfoodcompositiondatabase"
    }

    val xcfName = "feature:settings:database:swissfoodcompositiondatabaseKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.externaldatabase.swissfoodcompositiondatabase)
        implementation(projects.business.shared)
        implementation(projects.business.food)
    }
}
