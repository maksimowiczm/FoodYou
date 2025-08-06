plugins { `kotlin-dsl` }

group = "com.maksimowiczm.foodyou.platform.build_logic"

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
        register("library-business") {
            id = "com.maksimowiczm.foodyou.plugins.libraries.business"
            implementationClass =
                "com.maksimowiczm.foodyou.platform.buildlogic.plugins.BusinessLibraryConventionPlugin"
        }
    }
}
