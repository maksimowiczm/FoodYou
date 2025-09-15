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

includeBuild("FoodYouCore")

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

include(":shared:barcodescanner")

include(":navigation")

include(":business:opensource")

include(":feature:shared")

include(":feature:database:master")

include(":feature:database:externaldatabases")

include(":feature:database:databasedump")

include(":feature:database:swissfoodcompositiondatabase")

include(":feature:database:importcsvproducts")

include(":feature:home")

include(":feature:goals")

include(":feature:food:shared")

include(":feature:food:product")

include(":feature:food:recipe")

include(":feature:food:diary:shared")

include(":feature:food:diary:search")

include(":feature:food:diary:add")

include(":feature:food:diary:update")

include(":feature:food:diary:meal")

include(":feature:database:exportcsvproducts")

include(":feature:food:diary:quickadd")

include(":ui:shared")

include(":shared:resources")

include(":shared:compose")

include("ui:theme")

include(":business:shared")

include(":infrastructure:opensource")

include(":infrastructure:shared")

include(":ui:sponsor")

include(":ui:changelog")

include(":ui:about:opensource")

include(":ui:language")

include(":ui:personalization")

include(":ui:settings:opensource")

include(":ui:onboarding:opensource")
