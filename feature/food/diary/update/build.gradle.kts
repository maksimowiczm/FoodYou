plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.diary.update" }

    val xcfName = "feature:food:diary:updateKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(projects.business.fooddiary)

        implementation(projects.feature.food.shared)
        implementation(projects.feature.food.diary.shared)

        implementation(libs.kotlinx.datetime)
    }
}
