plugins { alias(libs.plugins.ui) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.food.diary.shared" }

    val xcfName = "ui:food:diary:sharedKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.core.shared)

        implementation(projects.ui.food.shared)

        implementation(libs.kotlinx.datetime)
    }
}
