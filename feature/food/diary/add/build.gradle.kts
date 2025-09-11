plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.diary.add" }

    val xcfName = "feature:food:diary:addKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.feature.food.shared)
        implementation(projects.feature.food.diary.shared)

        implementation(libs.kotlinx.datetime)
    }
}
