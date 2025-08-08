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
include(":feature:food")
include(":feature:measurement")
include(":feature:barcodescanner")
include(":feature:fooddiary")
include(":feature:swissfoodcompositiondatabase")
include(":feature:onboarding")
include(":feature:importexport")

include(":shared:common")
include(":shared:ui")
include(":navigation")
include(":business:shared")
include(":business:food")
include(":business:fooddiary")
include(":business:sponsorship")
include(":business:settings")
include(":feature3:shared")
include(":feature3:about:master")
include(":feature3:about:sponsor")
include(":feature3:settings:master")
include(":feature3:settings:meal")
include(":feature3:settings:language")
include(":feature3:settings:goals")
include(":feature3:settings:personalization")
include(":feature3:home:master")
include(":feature3:home")
include(":feature3:goals")
include(":externaldatabase:openfoodfacts")
include(":externaldatabase:usda")
include(":externaldatabase:swissfoodcompositiondatabase")
