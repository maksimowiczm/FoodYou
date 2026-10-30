import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.maksimowiczm.foodyou.swissfoodcomposition.compiler.MainKt")
}

dependencies {
    implementation(projects.core)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.androidx.room.runtime)
    implementation(libs.arrow.core)
    implementation(libs.kotlinx.serializationJson)
    implementation(libs.kotlinx.datetime)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
