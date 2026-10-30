import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.swissFoodComposition)
}

swissFoodComposition {
    dataFile.set(rootProject.file("swiss-food-composition/data/database.csv"))
    i18nDir.set(rootProject.file("swiss-food-composition/data/i18n"))
    dependencies.swissFoodCompositionCompiler(projects.swissFoodComposition.compiler)
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }

    android {
        namespace = "com.maksimowiczm.foodyou.swissfoodcomposition.compose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

        androidResources.enable = true
    }

    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.insert.koin.core)
            implementation(libs.vinceglb.filekitCore)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.swissfoodcomposition.generated.resources"
    generateResClass = always
}
