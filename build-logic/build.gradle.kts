plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    maven("https://repo.spring.io/snapshot")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.all.open)
    implementation(libs.kotlin.noarg)
    implementation(libs.spring.boot.gradle.plugin)
//    implementation(libs.spring.dependency.management)
}
