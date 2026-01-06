plugins {
    kotlin("jvm")
}
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    testImplementation(libs.findLibrary("mockk").get())
    testImplementation(libs.findLibrary("springmockk").get())
}
