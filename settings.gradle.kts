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
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "FoodYou"
include(":core-database")
include(":feature:goals")

include(":composeApp")
include(":core3")
include(":feature3:about")
include(":feature3:food")
include(":feature3:measurement")
include(":feature3:barcodescanner")
include(":feature3:fooddiary")
include(":feature3:openfoodfacts")
include(":feature3:calendar")
include(":feature3:language")
include(":feature3:usda")
include(":feature3:swissfoodcompositiondatabase")
include(":feature3:onboarding")
include(":feature3:importexport")
