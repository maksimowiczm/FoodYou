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
            implementation(projects.shared.ui)

            implementation(projects.feature3.about.master)
            implementation(projects.feature3.about.sponsor)

            implementation(projects.feature3.settings.master)
            implementation(projects.feature3.settings.meal)
            implementation(projects.feature3.settings.language)
            implementation(projects.feature3.settings.goals)
            implementation(projects.feature3.settings.personalization)
            implementation(projects.feature3.settings.database.master)
            implementation(projects.feature3.settings.database.externaldatabases)
            implementation(projects.feature3.settings.database.databasedump)

            implementation(projects.feature3.food.diary.search)
            implementation(projects.feature3.food.diary.add)
            implementation(projects.feature3.food.diary.update)

            implementation(projects.feature3.food.product)
            implementation(projects.feature3.food.recipe)

            implementation(projects.feature3.home)

            implementation(projects.feature3.goals)
        }
    }
}
