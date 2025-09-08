package com.maksimowiczm.foodyou.buildlogic.plugin

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class FoodYouLibrary : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            group = "com.maksimowiczm.foodyou"
            apply(plugin = libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(plugin = libs.findPlugin("kotlinMultiplatformAndroid").get().get().pluginId)
            apply(plugin = libs.findPlugin("kotlinSerialization").get().get().pluginId)
        }

        target.extensions.configure<KotlinMultiplatformExtension> {
            target.configureKotlinMultiplatform(this)
        }
    }

    @Suppress("UnstableApiUsage")
    internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) =
        extension.apply {
            androidLibrary {
                compileSdk = 36
                minSdk = 26

                withHostTestBuilder {}

                withDeviceTestBuilder { sourceSetTreeName = "test" }
                    .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
            }

            sourceSets.commonMain.dependencies {
                implementation(project(":shared"))
                implementation(libs.findLibrary("kotlinx-datetime").get())
                implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                implementation(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
}
