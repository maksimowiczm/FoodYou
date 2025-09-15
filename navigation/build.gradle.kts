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

            implementation(projects.ui.shared)

            implementation(projects.feature.settings.master)
            implementation(projects.feature.settings.language)
            implementation(projects.feature.settings.personalization)
            implementation(projects.feature.database.master)
            implementation(projects.feature.database.externaldatabases)
            implementation(projects.feature.database.databasedump)
            implementation(projects.feature.database.swissfoodcompositiondatabase)
            implementation(projects.feature.database.importcsvproducts)
            implementation(projects.feature.database.exportcsvproducts)

            implementation(projects.feature.food.diary.search)
            implementation(projects.feature.food.diary.add)
            implementation(projects.feature.food.diary.update)
            implementation(projects.feature.food.diary.meal)
            implementation(projects.feature.food.diary.quickadd)

            implementation(projects.feature.food.product)
            implementation(projects.feature.food.recipe)

            implementation(projects.feature.home)

            implementation(projects.feature.goals)

            implementation(projects.ui.theme)
            implementation(projects.ui.sponsor)
            implementation(projects.ui.about.opensource)

            // TODO get rid of business dependency
            implementation(libs.core.shared)
            implementation(libs.core.food)
        }
    }
}
