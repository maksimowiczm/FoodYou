plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(libs.compose.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("swissFoodComposition") {
            id = "com.maksimowiczm.foodyou.swiss-food-composition"
            implementationClass =
                "com.maksimowiczm.foodyou.swissfoodcomposition.plugin.SwissFoodCompositionPlugin"
            displayName = "Swiss Food Composition"
            description =
                "Compiles Swiss Food Composition data and registers it as a Compose resource."
        }
    }
}
