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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FoodYou"

include(":composeApp")

include(":shared:common")

include(":shared:ui")

include(":shared:barcodescanner")

include(":navigation")

include(":externaldatabase:openfoodfacts")

include(":externaldatabase:usda")

include(":externaldatabase:swissfoodcompositiondatabase")

include(":business:shared")

include(":business:food")

include(":business:fooddiary")

include(":business:sponsorship")

include(":business:settings")

include(":feature:shared")

include(":feature:about:master")

include(":feature:about:sponsor")

include(":feature:settings:master")

include(":feature:settings:meal")

include(":feature:settings:language")

include(":feature:settings:goals")

include(":feature:settings:personalization")

include(":feature:settings:database:master")

include(":feature:settings:database:externaldatabases")

include(":feature:settings:database:databasedump")

include(":feature:settings:database:swissfoodcompositiondatabase")

include(":feature:settings:database:importcsvproducts")

include(":feature:home")

include(":feature:goals")

include(":feature:food:shared")

include(":feature:food:product")

include(":feature:food:recipe")

include(":feature:food:diary:shared")

include(":feature:food:diary:search")

include(":feature:food:diary:add")

include(":feature:food:diary:update")

include(":feature:onboarding")
