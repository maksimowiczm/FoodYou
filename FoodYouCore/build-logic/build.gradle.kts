plugins { `kotlin-dsl` }

group = "com.maksimowiczm.foodyou.core.build_logic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

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
