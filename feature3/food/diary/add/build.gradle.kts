plugins { alias(libs.plugins.feature) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.feature.food.diary.add" }

    val xcfName = "feature3:food:diary:addKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(projects.business.shared)
        implementation(projects.business.fooddiary)
        implementation(projects.business.food)

        implementation(projects.feature3.food.shared)

        implementation(libs.kotlinx.datetime)
    }
}
