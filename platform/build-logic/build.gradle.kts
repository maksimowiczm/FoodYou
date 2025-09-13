plugins { `kotlin-dsl` }

dependencies {
    compileOnly(libs.gradle.android)
    compileOnly(libs.gradle.kotlin)
    compileOnly(libs.gradle.compose)
}

gradlePlugin {
    plugins {
        register("library-feature") {
            id = "com.maksimowiczm.foodyou.plugins.libraries.feature"
            implementationClass =
                "com.maksimowiczm.foodyou.platform.buildlogic.plugins.FeatureLibraryConventionPlugin"
        }

        register("library-ui") {
            id = "com.maksimowiczm.foodyou.plugins.libraries.ui"
            implementationClass =
                "com.maksimowiczm.foodyou.platform.buildlogic.plugins.UiLibraryConventionPlugin"
        }
    }
}
