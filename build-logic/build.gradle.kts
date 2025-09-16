plugins { `kotlin-dsl` }

dependencies {
    compileOnly(libs.gradle.android)
    compileOnly(libs.gradle.kotlin)
    compileOnly(libs.gradle.compose)
}

gradlePlugin {
    plugins {
        register("library-ui") {
            id = "com.maksimowiczm.foodyou.app.buildlogic.plugin.ui"
            implementationClass =
                "com.maksimowiczm.foodyou.app.buildlogic.plugin.UiLibraryConventionPlugin"
        }
    }
}
