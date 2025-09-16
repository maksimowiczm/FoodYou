plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    }

    val xcfName = "navigationKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(projects.shared.common)
            implementation(projects.shared.compose)

            implementation(projects.ui.goals)
            implementation(projects.ui.language)
            implementation(projects.ui.theme)
            implementation(projects.ui.personalization)
            implementation(projects.ui.sponsor)
            implementation(projects.ui.about.opensource)
            implementation(projects.ui.settings.opensource)
            implementation(projects.ui.onboarding.opensource)
            implementation(projects.ui.database.opensource)
            implementation(projects.ui.meal)
            implementation(projects.ui.home)
            implementation(projects.ui.food.product)
            implementation(projects.ui.food.recipe)
            implementation(projects.ui.food.diary.search)
            implementation(projects.ui.food.diary.add)
            implementation(projects.ui.food.diary.update)
            implementation(projects.ui.food.diary.quickadd)

            // TODO get rid of business dependency
            implementation(libs.core.shared)
            implementation(libs.core.food)
        }
    }
}
