plugins { `kotlin-dsl` }

dependencies {
    compileOnly(libs.gradle.android)
    compileOnly(libs.gradle.kotlin)
}

gradlePlugin {
    plugins {
        register("foodyou-library") {
            id = "com.maksimowiczm.foodyou.plugin.library"
            implementationClass = "com.maksimowiczm.foodyou.buildlogic.plugin.FoodYouLibrary"
        }
    }
}
