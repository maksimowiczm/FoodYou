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

include(":ui:goals")

include(":ui:database:opensource")

include(":ui:meal")

include(":ui:home")

include(":ui:food:shared")

include(":ui:food:product")

include(":ui:food:diary:shared")

include(":ui:food:diary:search")

include(":ui:food:recipe")

include(":ui:food:diary:add")

include(":ui:food:diary:quickadd")

include(":ui:food:diary:update")
