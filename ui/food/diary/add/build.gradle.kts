plugins { alias(libs.plugins.ui) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.app.ui.food.diary.add" }

    val xcfName = "ui:food:diary:addKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.core.shared)
        implementation(libs.core.food)
        implementation(libs.core.fooddiary)
        implementation(projects.business.shared)

        implementation(projects.ui.food.shared)
        implementation(projects.ui.food.diary.shared)

        implementation(libs.kotlinx.datetime)
    }
}
