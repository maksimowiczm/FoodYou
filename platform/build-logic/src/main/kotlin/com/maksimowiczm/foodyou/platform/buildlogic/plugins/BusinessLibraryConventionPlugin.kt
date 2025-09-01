package com.maksimowiczm.foodyou.platform.buildlogic.plugins

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class BusinessLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            apply(plugin = libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(
                plugin =
                    libs.findPlugin("android.kotlin.multiplatform.library").get().get().pluginId
            )
            apply(plugin = libs.findPlugin("kotlin.serialization").get().get().pluginId)
        }

        target.extensions.configure<KotlinMultiplatformExtension> {
            target.configureKotlinMultiplatform(this)
        }
    }

    internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) =
        extension.apply {
            androidLibrary {
                compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()
                minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()

                withHostTestBuilder {}

                withDeviceTestBuilder { sourceSetTreeName = "test" }
                    .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
            }

            sourceSets.apply {
                commonMain.dependencies {
                    implementation(libs.findBundle("business.library.implementation").get())
                    implementation(project(":shared:common"))
                    implementation(project(":business:shared"))
                }

                commonTest.dependencies { implementation(libs.findLibrary("kotlin.test").get()) }

                getByName("androidDeviceTest").dependencies {
                    implementation(libs.findLibrary("androidx.testRunner").get())
                    implementation(libs.findLibrary("androidx.testCore").get())
                    implementation(libs.findLibrary("androidx.testExt.junit").get())
                }
            }
        }
}
