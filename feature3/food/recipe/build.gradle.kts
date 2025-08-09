plugins {
    alias(libs.plugins.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.recipe" }

    val xcfName = "feature3:food:recipeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(projects.business.settings)
        implementation(projects.business.food)

        implementation(projects.feature3.food.shared)

        implementation(libs.compose.shimmer)
        implementation(libs.navigation.compose)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
    }
}
