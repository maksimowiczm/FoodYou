plugins {
    alias(libs.plugins.business)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.fooddiary" }

    val xcfName = "business:fooddiaryKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.paging.common)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
        }
    }
}
