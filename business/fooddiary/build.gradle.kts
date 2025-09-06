plugins { alias(libs.plugins.business) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.fooddiary" }

    val xcfName = "business:fooddiaryKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets.commonMain.dependencies {
        implementation(libs.core.shared)
        implementation(libs.core.fooddiary)
        implementation(libs.core.goals)

        implementation(libs.androidx.paging.common)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.androidx.room.runtime)
    }
}
