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
}

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
include(":app")
include(":core")
include(":core-database")
include(":core-model")
include(":core-domain")
include(":core-ui")
include(":feature:barcodescanner")
include(":feature:language")
include(":feature:calendar")
include(":feature:product")
include(":feature:recipe")
include(":feature:measurement")
include(":feature:meal")
include(":feature:goals")
include(":feature:addfood")
