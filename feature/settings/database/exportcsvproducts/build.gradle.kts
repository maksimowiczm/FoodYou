plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.settings.database.exportcsvproducts"
    }

    val xcfName = "feature:settings:database:exportcsvproductsKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.food)
        implementation(projects.business.shared)
        implementation(projects.feature.food.shared)
    }
}
