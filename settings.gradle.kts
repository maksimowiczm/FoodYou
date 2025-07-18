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
include(":app")
include(":core-database")
include(":core-model")
include(":core-domain")
include(":core-ui")
include(":feature:measurement")
include(":feature:meal")
include(":feature:goals")
include(":feature:importexport")
include(":feature:swissfoodcompositiondatabase")

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
