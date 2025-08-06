plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
}

kotlin {
    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.business.fooddiary"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

        withDeviceTestBuilder { sourceSetTreeName = "test" }
            .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    }

    val xcfName = "business:fooddiaryKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":business:shared"))
            implementation(project(":shared:common"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }
    }
}
