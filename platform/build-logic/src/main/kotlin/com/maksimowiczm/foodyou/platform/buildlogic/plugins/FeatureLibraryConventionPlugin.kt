package com.maksimowiczm.foodyou.platform.buildlogic.plugins

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class FeatureLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(
                plugin =
                    libs.findPlugin("android.kotlin.multiplatform.library").get().get().pluginId
            )
            apply(plugin = libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(plugin = libs.findPlugin("composeCompiler").get().get().pluginId)

            configureKotlinMultiplatform()
        }
    }

    internal fun Project.configureKotlinMultiplatform(
        kmp: KotlinMultiplatformExtension = extensions.getByType(),
        compose: ComposeExtension = extensions.getByType(),
    ) =
        kmp.apply {
            kmp.androidLibrary {
                compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()
                minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findLibrary("core.shared").get())
                    implementation(libs.findLibrary("core.food").get())
                    implementation(libs.findLibrary("core.fooddiary").get())
                    implementation(libs.findLibrary("core.goals").get())

                    implementation(project(":shared:common"))
                    implementation(project(":shared:compose"))
                    implementation(project(":shared:resources"))

                    implementation(project(":feature:shared"))
                    implementation(project(":ui:shared"))

                    implementation(project(":business:opensource"))

                    implementation(libs.findBundle("feature.library.implementation").get())

                    implementation(compose.dependencies.runtime)
                    implementation(compose.dependencies.foundation)
                    // implementation(compose.dependencies.material3)
                    implementation(libs.findLibrary("jetbrains.compose.material3").get())
                    implementation(compose.dependencies.materialIconsExtended)
                    implementation(compose.dependencies.ui)
                    implementation(compose.dependencies.components.resources)
                }
            }
        }
}
