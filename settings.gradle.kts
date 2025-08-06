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

includeBuild("platform/build-logic")

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
include(":composeApp")
include(":core")
include(":feature:about")
include(":feature:food")
include(":feature:measurement")
include(":feature:barcodescanner")
include(":feature:fooddiary")
include(":feature:openfoodfacts")
include(":feature:calendar")
include(":feature:language")
include(":feature:usda")
include(":feature:swissfoodcompositiondatabase")
include(":feature:onboarding")
include(":feature:importexport")

include(":shared:common")
include(":shared:ui")
include(":business:shared")
include(":business:food")
include(":business:fooddiary")
include(":business:sponsorship")
include(":shared:ui")
