plugins { alias(libs.plugins.business) }

kotlin {
    androidLibrary { namespace = "com.maksimowiczm.foodyou.business.fooddiary" }

    val xcfName = "business:fooddiaryKit"

    iosX64 { binaries.framework { baseName = xcfName } }

    iosArm64 { binaries.framework { baseName = xcfName } }

    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets { commonMain.dependencies { implementation(libs.androidx.paging.common) } }
}
