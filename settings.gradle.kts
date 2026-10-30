enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("swiss-food-composition/plugin")
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "FoodYou"

include(":core")

include(":app")

include(":androidApp")

include(":swiss-food-composition:compiler")

project(":swiss-food-composition:compiler").projectDir = file("swiss-food-composition/compiler")

include(":swiss-food-composition:compose")

project(":swiss-food-composition:compose").projectDir = file("swiss-food-composition/compose")
