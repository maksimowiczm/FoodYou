plugins { `kotlin-dsl` }

group = "com.maksimowiczm.foodyou.platform.build_logic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.gradle.android)
    compileOnly(libs.gradle.kotlin)
    compileOnly(libs.gradle.compose)
}

gradlePlugin {
    plugins {
        register("library-business") {
            id = "com.maksimowiczm.foodyou.plugins.libraries.business"
            implementationClass =
                "com.maksimowiczm.foodyou.platform.buildlogic.plugins.BusinessLibraryConventionPlugin"
        }

        register("library-feature") {
            id = "com.maksimowiczm.foodyou.plugins.libraries.feature"
            implementationClass =
                "com.maksimowiczm.foodyou.platform.buildlogic.plugins.FeatureLibraryConventionPlugin"
        }
    }
}
