val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.findLibrary("kotlin.logging.jvm").get())
}
