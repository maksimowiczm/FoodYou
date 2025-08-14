package com.maksimowiczm.foodyou.platform.buildlogic.plugins

import com.android.build.api.dsl.androidLibrary
import kotlin.apply
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
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
        }

        target.extensions.configure<KotlinMultiplatformExtension> {
            val kmp = this
            target.extensions.configure<ComposeExtension> {
                target.configureKotlinMultiplatform(kmp, this)
            }
        }
    }

    internal fun Project.configureKotlinMultiplatform(
        kmp: KotlinMultiplatformExtension,
        compose: ComposeExtension,
    ) =
        kmp.apply {
            kmp.androidLibrary {
                compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()
                minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()

                withHostTestBuilder {}

                withDeviceTestBuilder { sourceSetTreeName = "test" }
                    .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(project(":shared:common"))
                    implementation(project(":shared:ui"))
                    implementation(project(":feature:shared"))

                    implementation(libs.findBundle("feature.library.implementation").get())

                    implementation(compose.dependencies.runtime)
                    implementation(compose.dependencies.foundation)
                    // implementation(compose.dependencies.material3)
                    implementation(libs.findLibrary("jetbrains.compose.material3").get())
                    implementation(compose.dependencies.materialIconsExtended)
                    implementation(compose.dependencies.ui)
                    implementation(compose.dependencies.components.resources)
                }

                commonTest.dependencies { implementation(libs.findLibrary("kotlin.test").get()) }

                getByName("androidDeviceTest").dependencies {
                    implementation(libs.findLibrary("androidx.runner").get())
                    implementation(libs.findLibrary("androidx.test.core").get())
                    implementation(libs.findLibrary("androidx.junit").get())
                }
            }
        }
}
