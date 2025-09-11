plugins {
    alias(libs.plugins.feature)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.recipe" }

    val xcfName = "feature:food:recipeKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.feature.food.shared)

        implementation(libs.compose.shimmer)
        implementation(libs.navigation.compose)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
    }
}
